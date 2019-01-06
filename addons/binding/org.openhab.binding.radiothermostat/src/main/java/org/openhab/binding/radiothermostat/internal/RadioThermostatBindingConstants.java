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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link RadioThermostatBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author John M. Gardner - Initial contribution
 */
@NonNullByDefault
public class RadioThermostatBindingConstants {

    private static final String BINDING_ID = "radiothermostat";
    private static final String THING_CT80 = "ct80";

    public static final Double CACHE_TIME = 60000d;

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_CT80 = new ThingTypeUID(BINDING_ID, THING_CT80);

    // List of all Channel ids
    public static final String CHANNEL_TEMP_INDOOR = "tempIndoor";
    public static final String CHANNEL_TMODE = "tmode";
    public static final String CHANNEL_HUMIDITY_INDOOR = "humidityIndoor";
    public static final String CHANNEL_FMODE = "fmode";
    public static final String CHANNEL_OVERRIDE = "override";
    public static final String CHANNEL_TARGET_HEAT = "t_heat";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_CT80);
    public static final Set<String> SUPPORTED_CHANNEL_IDS = Stream.of(CHANNEL_TEMP_INDOOR, CHANNEL_TMODE,
            CHANNEL_HUMIDITY_INDOOR, CHANNEL_FMODE, CHANNEL_OVERRIDE, CHANNEL_TARGET_HEAT).collect(Collectors.toSet());

}
