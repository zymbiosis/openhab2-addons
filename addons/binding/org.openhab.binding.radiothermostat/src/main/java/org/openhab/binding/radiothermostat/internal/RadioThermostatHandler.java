/**
 * Copyright (c) 2014,2019 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.radiothermostat.internal;

import static org.openhab.binding.radiothermostat.internal.RadioThermostatBindingConstants.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.Temperature;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.library.unit.ImperialUnits;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.radiothermostat.internal.models.FMode;
import org.openhab.binding.radiothermostat.internal.models.FState;
import org.openhab.binding.radiothermostat.internal.models.TMode;
import org.openhab.binding.radiothermostat.internal.models.Tstat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link RadioThermostatHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author John M. Gardner - Initial contribution
 */
@NonNullByDefault
public class RadioThermostatHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(RadioThermostatHandler.class);
    private ScheduledFuture<?> updateJob;
    private Gson gson;
    private Tstat tstat;

    @Nullable
    private RadioThermostatConfiguration config;

    public RadioThermostatHandler(Thing thing) {
        super(thing);
        gson = new Gson();
        tstat = new Tstat();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            // Check if we need a refresh
            if (tstat.getLastUpdate() + CACHE_TIME < System.currentTimeMillis()) {
                getThermostatData();
            }
            updateChannel(channelUID.getId());
        } else {
            // Must be an update
            handleSetChannel(channelUID, command);
        }
    }

    private void handleSetChannel(ChannelUID channelUID, Command command) {
        // First set the HTTP POST
        URI uri;
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.setConnectTimeout(60000);
            httpClient.start();
            uri = new URI("http", config.ipAddress, "/tstat", null);
            Request request = httpClient.newRequest(uri);
            request.method(HttpMethod.POST);
            request.header("Content-Type", "application/json");

            // httpClient.newRequest(getURL(path)).timeout(config.getAsyncTimeout(), TimeUnit.SECONDS).method(POST)
            // .onResponseSuccess(postExchange).onResponseFailure(postExchange) // .onComplete(postExchange)
            // .content(new StringContentProvider(addSID(args), StandardCharsets.UTF_8)).send(postExchange);

            // post.addHeader("Content-Type", "application/json");

            switch (channelUID.getId()) {
                case CHANNEL_TMODE:
                    tstat.setTmode(TMode.fromInt(Integer.valueOf(command.toString())));
                    request.content(new StringContentProvider("{\"tmode\" : " + tstat.getTmode().getValue() + "}"));
                    break;
                case CHANNEL_FMODE:
                    tstat.setFmode(FMode.fromInt(Integer.valueOf(command.toString())));
                    request.content(new StringContentProvider("{\"fmode\" : " + tstat.getFmode().getValue() + "}"));
                    break;
                case CHANNEL_TARGET_HEAT:
                    tstat.setT_heat(((QuantityType<Temperature>) command).toUnit(ImperialUnits.FAHRENHEIT)
                            .toBigDecimal().setScale(0, RoundingMode.HALF_UP).doubleValue());
                    request.content(
                            new StringContentProvider("{\"it_heat\" : " + Double.toString(tstat.getT_heat()) + "}"));
                    break;
                case CHANNEL_TARGET_COOL:
                    tstat.setT_cool(((QuantityType<Temperature>) command).toUnit(ImperialUnits.FAHRENHEIT)
                            .toBigDecimal().setScale(0, RoundingMode.HALF_UP).doubleValue());
                    request.content(
                            new StringContentProvider("{\"it_cool\" : " + Double.toString(tstat.getT_cool()) + "}"));
                    break;
                case CHANNEL_HOLD:
                    tstat.setHold(command == OnOffType.ON ? 1 : 0);
                    request.content(
                            new StringContentProvider("{\"hold\" : " + Integer.toString(tstat.getHold()) + "}"));
                    break;
            }

            ContentResponse response = request.send();
            logger.debug("Thermostat Response: " + response.toString());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        config = getConfigAs(RadioThermostatConfiguration.class);

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN); // Just temporarily

        // See if we get a response back from the thermostat
        scheduler.execute(() -> {
            InetAddress address;
            boolean thingReachable = false;
            try {
                address = InetAddress.getByName(config.ipAddress);
                thingReachable = address.isReachable(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
                scheduleUpdate();
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    private void scheduleUpdate() {
        if (updateJob == null || updateJob.isCancelled()) {
            Runnable runnable = () -> {
                try {
                    // Request new data from the thermostat
                    getThermostatData();

                    // Trigger an update for all channels
                    for (Channel channel : getThing().getChannels()) {
                        updateChannel(channel.getUID().getId());
                    }
                } catch (Exception e) {
                    logger.error("Exception in scheduleUpdate: {}", e.getMessage(), e);
                }
            };

            updateJob = scheduler.scheduleWithFixedDelay(runnable, 0, CACHE_TIME.longValue(), TimeUnit.MILLISECONDS);
        }
    }

    private void getThermostatData() {
        if (tstat.getLastUpdate() + CACHE_TIME >= System.currentTimeMillis()) {
            return; // We've been here recently, so lets pass
        }

        String errorMsg = null;

        try {
            // First get the HTTP request to get the JSON data
            URI uri = new URI("http", config.ipAddress, "/tstat", null);
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();

            try {
                String tstatResponse = IOUtils.toString(connection.getInputStream());
                logger.debug("TSTAT = {}", tstatResponse);

                // Now convert the JSON string to an object
                tstat = gson.fromJson(tstatResponse, Tstat.class);
                tstat.setLastUpdate(System.currentTimeMillis());
            } finally {
                IOUtils.closeQuietly(connection.getInputStream());
            }
            // Lets go again and get the humidity
            uri = new URI("http", config.ipAddress, "/tstat/humidity", null);
            url = uri.toURL();
            connection = url.openConnection();

            try {
                String tstatResponse = IOUtils.toString(connection.getInputStream());
                logger.debug("TSTAT = {}", tstatResponse);

                // Now convert the JSON string to an object
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(tstatResponse).getAsJsonObject();
                tstat.setHumidity(obj.get("humidity").getAsDouble());
                tstat.setLastUpdate(System.currentTimeMillis());
            } finally {
                IOUtils.closeQuietly(connection.getInputStream());
            }
            return;
        } catch (MalformedURLException e) {
            logger.warn("Hostname or IP not valid: {}", e.getMessage());
        } catch (JsonSyntaxException e) {
            logger.warn("Unexpected or invalid response from thermostat: {}", e.getMessage());
        } catch (URISyntaxException e) {
            logger.warn("Hostname or IP not valid: {}", e.getMessage());
        } catch (IOException e) {
            logger.warn("{}", e.getMessage());
        }

        // TODO: updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, errorMsg);
        return;
    }

    private void updateChannel(String channelId) {
        if (isLinked(channelId)) {
            Object value;
            try {
                value = getValue(channelId);
            } catch (Exception e) {
                logger.debug("Unknown element {}", channelId.toUpperCase());
                return;
            }

            State state = null;
            if (value == null) {
                state = UnDefType.UNDEF;
            } else if (value instanceof Boolean) {
                state = new StringType(value.toString());
            } else if (value instanceof Double) {
                state = new DecimalType((Double) value);
            } else if (value instanceof BigDecimal) {
                state = new DecimalType((BigDecimal) value);
            } else if (value instanceof Integer) {
                state = new DecimalType(BigDecimal.valueOf(((Integer) value).longValue()));
            } else if (value instanceof QuantityType) {
                state = new QuantityType<Temperature>(value.toString()).toUnit(ImperialUnits.FAHRENHEIT);
            } else if (value instanceof String) {
                state = new StringType(value.toString());
            } else if (value instanceof TMode) {
                state = new StringType(value.toString());
            } else if (value instanceof FMode) {
                state = new StringType(value.toString());
            } else if (value instanceof FState) {
                state = new StringType(value.toString());
            } else {
                logger.warn("Update channel {}: Unsupported value type {}", channelId,
                        value.getClass().getSimpleName());
            }
            logger.debug("Update channel {} with state {} ({})", channelId, (state == null) ? "null" : state.toString(),
                    (value == null) ? "null" : value.getClass().getSimpleName());

            // Update the channel
            if (state != null) {
                updateState(channelId, state);
            }
        }
    }

    public @Nullable Object getValue(String channelId) throws Exception {
        String[] fields = StringUtils.split(channelId, "#");

        if (tstat != null) {
            switch (fields[0]) {
                case CHANNEL_TEMP_INDOOR:
                    return new QuantityType<>(tstat.getTemp(), ImperialUnits.FAHRENHEIT);
                case CHANNEL_HUMIDITY_INDOOR:
                    return tstat.getHumidity();
                case CHANNEL_TMODE:
                    return tstat.getTmode().getValue();
                case CHANNEL_TSTATE:
                    return tstat.getTstate().getValue();
                case CHANNEL_FMODE:
                    return tstat.getFmode().getValue();
                case CHANNEL_FSTATE:
                    return tstat.getFstate().getValue();
                case CHANNEL_OVERRIDE:
                    return tstat.getOverride();
                case CHANNEL_HOLD:
                    return tstat.getHold();
                case CHANNEL_TARGET_HEAT:
                    return new QuantityType<>(tstat.getT_heat(), ImperialUnits.FAHRENHEIT);
                case CHANNEL_TARGET_COOL:
                    return new QuantityType<>(tstat.getT_cool(), ImperialUnits.FAHRENHEIT);
            }
        }

        return null;
    }
}
