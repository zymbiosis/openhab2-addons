# RadioThermostat Binding

This binding integrates with RadioThermostat wifi-enabled smart thermostats. 

## Supported Things

Currently this binding was only tested with the CT-80 version of the Radio Thermostat, but should work with other versions.

## Discovery

_WIP_

## Binding Configuration

_If your binding requires or supports general configuration settings, please create a folder ```cfg``` and place the configuration file ```<bindingId>.cfg``` inside it. In this section, you should link to this file and provide some information about the options. The file could e.g. look like:_

```
# Configuration for the Philips Hue Binding
#
# Default secret key for the pairing of the Philips Hue Bridge.
# It has to be between 10-40 (alphanumeric) characters 
# This may be changed by the user for security reasons.
secret=EclipseSmartHome
```

_Note that it is planned to generate some part of this based on the information that is available within ```ESH-INF/binding``` of your binding._

_If your binding does not offer any generic configurations, you can remove this section completely._

## Thing Configuration

The only information necessary is the IP address or hostname of your thermostat. Examples following will reference the default thing name of `tstat`.  Setting up the thing could be done like:

```
radiothermostat:ct80:tstat [ipAddress="192.168.1.10"]
```

## Channels

Here is a list of all the supported channels:

| Channel Type ID           | Item Type | Description                                                                                                      |
|---------------------------|-----------|------------------------------------------------------------------------------------|
| tempIndoor                | Number    | Indoor temperature as reported by the thermostat |
| humidityIndoor            | Number    | Indoor humidity as reported by the thermostat (may be model specific)|
| t_heat                    | Number    | Heat Target |
| t_cool                    | Number    | Cool Target |
| tMode                     | Number    | Thermostat Mode |
| tState                    | Number    | The current Thermostat state|
| fMode                     | Number    | Fan Mode |
| fState                    | Number    | The current Fan state |
| override                  | String    | Is the thermostat being overridden |
| hold                      | Number    | Temperature Hold |

## Item Configuration

tstat.items

```
Group Thermostat <heating> 
Number rtsTempIndoor      "Inside Temp"      <temperature> (Thermostat) { channel="radiothermostat:ct80:tstat:tempIndoor" }
Number rtsHumidityIndoor  "Inside Humidity"  <humidity>    (Thermostat) { channel="radiothermostat:ct80:tstat:humidityIndoor" }
Number rtsT_heat          "Target Heat"      <fire>        (Thermostat) { channel="radiothermostat:ct80:tstat:t_heat" }
Number rtsT_cool          "Target Cool"      <snow>        (Thermostat) { channel="radiothermostat:ct80:tstat:t_cool" }
Number rtsTmode           "Mode [%s]"        <heating>     (Thermostat) { channel="radiothermostat:ct80:tstat:tmode" } 
Number rtsTstate          "State [%s]"       <heating>     (Thermostat) { channel="radiothermostat:ct80:tstat:tstate" }
Number rtsFmode           "Fan Mode [%s]"    <fan>         (Thermostat) { channel="radiothermostat:ct80:tstat:fmode" } 
Number rtsFstate          "Fan State [%s]"   <fan>         (Thermostat) { channel="radiothermostat:ct80:tstat:fstate" }
Switch rtsHold            "Hold"             <switch>      (Thermostat) { channel="radiothermostat:ct80:tstat:hold" }
Number rtsOverride        "Override [%s]"                  (Thermostat) { channel="radiothermostat:ct80:tstat:override" }
```

## Sitemap Configuration

tstat.sitemap

```
sitemap tstat label="Thermostat"
{
    Frame {
        Text item=rtsTempIndoor
        Text item=rtsHumidityIndoor
        Setpoint item=rtsT_heat
        Setpoint item=rtsT_cool
        Switch item=rtsTmode mappings=[0='Off',1='Heat',2='Cool',3='Auto']
        Text item=rtsTstate
        Switch item=rtsFmode mappings=[0='Auto',1='Circ',2='On']
        Text item=rtsFstate
        Switch item=rtsHold
        Text item=rtsOverride
    }
}
```
