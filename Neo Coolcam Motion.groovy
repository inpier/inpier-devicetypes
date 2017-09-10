/**
 *  Device Type Definition File
 *
 *  Device Type:        Neo CoolCam Motion Sensor
 *  File Name:          Neo CoolCam Motion Sensor.groovy
 *  Initial Release:    2017-09-08
 *  Author:             Inpier
 *  (Minor moificationss to Cyril Peponnet's Fibaro Motion Sensor)
 *
 *  Based on :-
 *  Device Type:        Fibaro Motion Sensor
 *  File Name:          FibarMotionSensor.groovy
 *  Initial Release:    2015-06-23
 *  Author:             Cyril Peponnet
 *  Email:              cyril@peponnet.fr
 *
 *  Copyright 2015 Cyril Peponnet
 *             
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 ***************************************************************************************
*/

/**
 * Sets up metadata, preferences simulator info and tile definition.
 *
 */

metadata {
    definition (name: "Neo CoolCam Motion Sensor", namespace: "inpier", author: "Cyril Peponnet") {

        attribute   "needUpdate", "string"

        capability  "Acceleration Sensor"
        capability  "Battery"
        capability  "Configuration"
        capability  "Illuminance Measurement"
        capability  "Motion Sensor"
        capability  "Sensor"
        capability  "Temperature Measurement"

        //fingerprint deviceId: "0x2001", inClusters: "0x30,0x84,0x85,0x80,0x8F,0x56,0x72,0x86,0x70,0x8E,0x31,0x9C,0xEF,0x30,0x31,0x9C"
        //zw:S type:0701 mfr:0258 prod:0003 model:1083 ver:3.93 zwv:4.05 lib:06 cc:5E,86,72,5A,73,80,31,71,30,70,85,59,84 role:06 ff:8C07 ui:8C07
        fingerprint mfr: "0258", prod: "0003", model: "1083", deviceJoinName: "Neo Coolcam Motion Sensor"
    }

    preferences {
        input description: "Once you change values on this page, the `Synced` Status will become `pending` status.\
                            You can then force the sync by triple clicking the code-button inside the device or wait for the\
                            next WakeUp (every 2 hours).",

              displayDuringSetup: false, type: "paragraph", element: "paragraph"

        generate_preferences(configuration_model())
    }

    simulator {

        status "motion (basic)"     : zwave.basicV1.basicSet(value:0xFF).incomingMessage()
        status "no motion (basic)"  : zwave.basicV1.basicSet(value:0).incomingMessage()
        status "motion (binary)"    : zwave.sensorBinaryV2.sensorBinaryReport(sensorType:12, sensorValue:0xFF).incomingMessage()
        status "no motion (binary)" : zwave.sensorBinaryV2.sensorBinaryReport(sensorType:12, sensorValue:0).incomingMessage()
        status "wake up"            : "command: 8407, payload: "

        for (int i = 0; i <= 100; i += 20) {
            status "Vibration ${i}%": zwave.sensorAlarmV1.sensorAlarmReport(
                seconds: 30, sensorType: 0x00 /*general purpose */, sensorState:i, sourceNodeId: 2).incomingMessage()
        }

        for (int i = 0; i <= 100; i += 20) {
            status "temperature ${i}F": new physicalgraph.zwave.Zwave().sensorMultilevelV2.sensorMultilevelReport(
                scaledSensorValue: i, precision: 1, sensorType: 1, scale: 1).incomingMessage()
        }

        for (int i = 200; i <= 1000; i += 200) {
            status "luminance ${i} lux": new physicalgraph.zwave.Zwave().sensorMultilevelV2.sensorMultilevelReport(
                scaledSensorValue: i, precision: 0, sensorType: 3).incomingMessage()
        }

        for (int i = 0; i <= 100; i += 20) {
            status "battery ${i}%": new physicalgraph.zwave.Zwave().batteryV1.batteryReport(
                batteryLevel: i).incomingMessage()
        }
    }

    tiles {
        standardTile("motion", "device.motion", width: 3, height: 2) {
            state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
            state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
        }
       
        valueTile("illuminance", "device.illuminance", inactiveLabel: false) {
            state "luminosity", label:'${currentValue} lux', unit:"lux"
        }
        valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat") {
            state "battery", label:'${currentValue}% battery', unit:""
        }
        
        standardTile("configure", "device.needUpdate", inactiveLabel: false) {
            state "NO" , label:'Synced', action:"configuration.configure", icon:"st.secondary.refresh-icon", backgroundColor:"#99CC33"
            state "YES", label:'Pending', action:"configuration.configure", icon:"st.secondary.refresh-icon", backgroundColor:"#CCCC33"
        }
        main(["motion", "illuminance"])
        details(["motion", "illuminance", "configure", "battery"])
    }
}

/**
* This function generate the preferences menu from the XML file
* each input will be accessible from settings map object.
*/
def generate_preferences(configuration_model)
{
    def configuration = parseXml(configuration_model)
    configuration.Value.each
    {
        switch(it.@type)
        {
            case ["byte","short"]:
                input "${it.@index}", "number",
                    title:"${it.@index} - ${it.@label}\n" + "${it.Help}",
                    defaultValue: "${it.@value}"
            break
            case "list":
                def items = []
                it.Item.each { items << ["${it.@value}":"${it.@label}"] }
                input "${it.@index}", "enum",
                    title:"${it.@index} - ${it.@label}\n" + "${it.Help}",
                    defaultValue: "${it.@value}",
                    options: items
            break
        }
    }
}

/**
* Parse incoming device messages to generate events
*/
def parse(String description)
{
    log.debug "==> New Zwave Event: ${description}, Battery: ${state.lastBatteryReport}"
	log.debug("RAW command: $description")
    def result = []

    switch(description) {
        case ~/Err.*/:
            log.error "Error: $description"
        break
        // updated is hit when the device is paired.
        case "updated":
            result << response(zwave.wakeUpV1.wakeUpIntervalSet(seconds: 7200, nodeid:zwaveHubNodeId).format())
            result << response(zwave.batteryV1.batteryGet().format())
            result << response(zwave.versionV1.versionGet().format())
            result << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format())
            result << response(zwave.firmwareUpdateMdV2.firmwareMdGet().format())
            result << response(configure())
        break
											//:5E,86,72,5A,73,80,31,71,30,70,85,59,84
		default:
            def cmd = zwave.parse(description, [0x72: 2, 0x31: 2, 0x30: 1, 0x84: 1, 0x9C: 1, 0x70: 2, 0x80: 1, 0x86: 1, 0x7A: 1, 0x56: 1])
            if (cmd) {
                result += zwaveEvent(cmd)
            }
        break
    }

    log.debug "=== Parsed '${description}' to ${result.inspect()}"
    if ( result[0] != null ) { result }
}

/**
* Handle and decode encapsulated cmds
*/
def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd)
{
    def versions = [0x31: 2, 0x30: 1, 0x84: 1, 0x9C: 1, 0x70: 2]
    // def encapsulatedCommand = cmd.encapsulatedCommand(versions)
    def version = versions[cmd.commandClass as Integer]
    def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
    def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)
    if (!encapsulatedCommand) {
        log.debug "Could not extract command from $cmd"
    } else {
        //log.debug "Encapsulated Command $cmd -> $encapsulatedCommand"
        zwaveEvent(encapsulatedCommand)
    }
}

/**
* Sensors Events
*/
def motionValueEvent(Short value, String message)
{
    def description = value ? "$device.displayName detected $message " : "$device.displayName $message has stopped"
    def returnValue = value ? "active" : "inactive"
    createEvent([name: "motion", value: returnValue, descriptionText: description ])
}

/**
* Zwave events overload
*/
def zwaveEvent(physicalgraph.zwave.commands.sensoralarmv1.SensorAlarmReport cmd)
{
    log.debug "%%%% Sensor Alarm Report"
    def returnValue = ""
    def message = ""
    switch(cmd.sensorState) {
        case 0:
            returnValue = "still"
            message     = "$device.displayName is now still"
        break
        case 255:
            returnValue = "moving"
            message     = "$device.displayName is moving"
        break
        default:
            returnValue = cmd.sensorState
            message     = "$device.displayName detected some vibrations"
        break
    }
    createEvent([ name: "acceleration", value: returnValue , descriptionText: message ])
}

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd) {
    log.debug "%%%% Sensor Binary Report"
    motionValueEvent(cmd.sensorValue, "motion")
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd)
{
    log.debug "%%%% Basic Set Cmd"
    motionValueEvent(cmd.value, "motion")
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv2.SensorMultilevelReport cmd)
{
    log.debug "%%%% Multi Sensor Report"
    def map = [ displayed: true ]
    switch (cmd.sensorType) {
        case 1:
            map.name = "temperature"
            def cmdScale = cmd.scale == 1 ? "F" : "C"
			map.value = Math.round(convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale,  cmd.precision).toFloat()).toString()
			map.unit  = getTemperatureScale()
            break;
        case 3:
            map.name = "illuminance"
            map.value = cmd.scaledSensorValue.toInteger().toString()
            map.unit = "lux"
            break;
    }
    createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
    def map = [ name: "battery", unit: "%" ]
    if (cmd.batteryLevel == 0xFF)
    {
        map.value = 1
        map.descriptionText = "${device.displayName} battery is low"
        map.isStateChange = true
    }
    else
    {
        map.value = cmd.batteryLevel
    }
    state.lastBatteryReport = now()
    createEvent(map)
}

/**
* This is called each time your device will wake up.
*/
def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd)
{
    log.debug "%%%% Device ${device.displayName} woke up"
    def commands = sync_properties()
    sendEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)
    // check if we need to request battery level (every 48h)
    if (!state.lastBatteryReport || (now() - state.lastBatteryReport)/60000 >= 60 * 48)
    {
        commands << zwave.batteryV1.batteryGet().format()
    }
    // Adding No More infomration needed at the end
    commands << zwave.wakeUpV1.wakeUpNoMoreInformation().format()
    response(delayBetween(commands, 1500))
}


/**
* This will be called each time we update a paramater. Use it to fill our currents parameters as a callback
*/
def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
    update_current_properties(cmd)
    log.debug "${device.displayName} parameter '${cmd.parameterNumber}' with a byte size of '${cmd.size}' is set to '${cmd.configurationValue}'"
}

/**
* This is called the first time upon association,
*/

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {

    def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
    def result = []

    log.debug "msr: $msr"
    updateDataValue("MSR", msr)
    result << createEvent(descriptionText: "$device.displayName MSR: $msr", isStateChange: false)

    if ( msr == "010F-0800-2001" )
    {
        log.debug "Dealing with a Fibaro Motion Sensor."
        result << response(configure())
    }

    result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.debug "Catchall reached for cmd: ${cmd.toString()}}"
    createEvent(displayed: false, descriptionText: "$device.displayName: $cmd")
}

/**
* Only for information purpose upon association
*/
def createEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd, Map item1) {
    log.debug "manufacturerId:   ${cmd.manufacturerId}"
    log.debug "manufacturerName: ${cmd.manufacturerName}"
    log.debug "productId:        ${cmd.productId}"
    log.debug "productTypeId:    ${cmd.productTypeId}"
}

def createEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd, Map item1) {
    updateDataValue("applicationVersion", "${cmd.applicationVersion}")
    log.debug "applicationVersion:      ${cmd.applicationVersion}"
    log.debug "applicationSubVersion:   ${cmd.applicationSubVersion}"
    log.debug "zWaveLibraryType:        ${cmd.zWaveLibraryType}"
    log.debug "zWaveProtocolVersion:    ${cmd.zWaveProtocolVersion}"
    log.debug "zWaveProtocolSubVersion: ${cmd.zWaveProtocolSubVersion}"
}

def createEvent(physicalgraph.zwave.commands.firmwareupdatemdv1.FirmwareMdReport cmd, Map item1) {
    log.debug "checksum:       ${cmd.checksum}"
    log.debug "firmwareId:     ${cmd.firmwareId}"
    log.debug "manufacturerId: ${cmd.manufacturerId}"
}

/**
* Triggered when Done button is pushed on Preference Pane
*/
def updated()
{
    // Only used to toggle the status if update is needed
    update_needed_settings()
    sendEvent(name:"needUpdate", value: device.currentValue("needUpdate"), displayed:false, isStateChange: true)
}

/**
* Update current cache properties
*/
def update_current_properties(cmd)
{
    def currentProperties = state.currentProperties ?: [:]

    currentProperties."${cmd.parameterNumber}" = cmd.configurationValue

    if (settings."${cmd.parameterNumber}" != null)
    {
        if (settings."${cmd.parameterNumber}".toInteger() == cmd2Integer(cmd.configurationValue))
        {
            sendEvent(name:"needUpdate", value:"NO", displayed:false, isStateChange: true)
        }
        else
        {
            sendEvent(name:"needUpdate", value:"YES", displayed:false, isStateChange: true)
        }
    }

    state.currentProperties = currentProperties
}

/**
* Update needed settings
*/
def update_needed_settings()
{
    def cmds = []
    def currentProperties = state.currentProperties ?: [:]
    def configuration = parseXml(configuration_model())
    def isUpdateNeeded = "NO"
    configuration.Value.each
    {
        if (currentProperties."${it.@index}" == null)
        {
            log.debug "Current value of parameter ${it.@index} is unknown"
            isUpdateNeeded = "YES"
        }
        else if (settings."${it.@index}" != null && cmd2Integer(currentProperties."${it.@index}") != settings."${it.@index}".toInteger())
        {
            log.debug "Parameter ${it.@index} will be updated to " + settings."${it.@index}"
            isUpdateNeeded = "YES"
            switch(it.@type)
            {
                case ["byte", "list"]:
                    cmds << zwave.configurationV1.configurationSet(configurationValue: [(settings."${it.@index}").toInteger()], parameterNumber: it.@index.toInteger(), size: 1).format()
                break
                case "short":
                    def short valueLow   = settings."${it.@index}" & 0xFF
                    def short valueHigh = (settings."${it.@index}" >> 8) & 0xFF
                    def value = [valueHigh, valueLow]
                    cmds << zwave.configurationV1.configurationSet(configurationValue: value, parameterNumber: it.@index.toInteger(), size: 2).format()
                break
            }
            cmds << zwave.configurationV1.configurationGet(parameterNumber: it.@index.toInteger()).format()
        }
    }
    sendEvent(name:"needUpdate", value: isUpdateNeeded, displayed:false, isStateChange: true)

    return cmds
}

/**
* Try to sync properties with the device
*/
def sync_properties()
{
    
    def currentProperties = state.currentProperties ?: [:]
    def configuration = parseXml(configuration_model())

    def cmds = []
    configuration.Value.each
    {
        if (! currentProperties."${it.@index}" || currentProperties."${it.@index}" == null)
        {
            log.debug "Looking for current value of parameter ${it.@index}"
            cmds << zwave.configurationV1.configurationGet(parameterNumber: it.@index.toInteger()).format()
        }
    }

    if (device.currentValue("needUpdate") == "YES") { cmds += update_needed_settings() }
    return cmds
}

/**
* Configures the device to settings needed by SmarthThings at device discovery time.
* Need a triple click on B-button to zwave commands to pass
*/
def configure() {
    log.debug "Configuring Device For SmartThings Use"
    def cmds = []

    // Associate Group 3 Device Status (Group 1 is for Basic direct action -switches-, Group 2 for Tamper Alerts System -alarm-)
    // Hub need to be Associate to group 3
    cmds << zwave.associationV2.associationSet(groupingIdentifier:3, nodeId:[zwaveHubNodeId]).format()
    cmds += sync_properties()
    delayBetween(cmds , 1500)
}

/**
* Convert 1 and 2 bytes values to integer
*/
def cmd2Integer(array) { array.size() == 1 ? array[0] : ((array[0] & 0xFF) << 8) | (array[1] & 0xFF) }

/**
* Define the Neo CoolCam motion senssor model used to generate preference pane.
*/
def configuration_model()
{
'''
<configuration>
  <Value type="byte" index="1" label="SENSITIVITY LEVEL SETTING" min="8" max="255" value="12">
    <Help>
This parameter defines the sensitivity of PIR detector, it is recommended to test the
detector with movements from a farthest end of the coverage area at first time of use.
If movements cannot be detected sensitively, simply adjust the sensitivity level with
this parameter. This parameter can be configured with the value of 8 through 255, where
8 means high sensitivity and 255 means lowest sensitivity.
Available settings: 8 - 255
Default setting: 12
    </Help>
  </Value>
  <Value type="short" index="2" label="ON/OFF DURATION" min="5" max="600" value="30">
    <Help>
This parameter can determine how long the associated devices should stay ON
status. For instance, if this parameter is set to 30(second), the PIR detector will send a
BASIC SET Command to an associated device with value basic set level if PIR
detector is triggered and the associated device will be turned on for 30(second) before it
is turned off. This Parameter value must be large than Parameter 6#.
Available settings: 5 - 600
Default setting: 30 seconds
    </Help>
  </Value>
<Value type="byte" index="3" label="BASIC SET LEVEL" min="0" max="255" value="99">
    <Help>
Basic Set Command will be sent where contains a value when PIR detector is
triggered, the receiver will take it for consideration; for instance, if a lamp module is
received the Basic Set Command of which value is decisive as to how bright of dim
level of lamp module shall be. This
Parameter is used to some associated devices.

Available settings: 0 - 99 or 255
Default setting: 99
</Help>
    </Value>


  <Value type="list" index="4" label="PIR DETECTING FUNCTION ENABLED/DISABLED" min="0" max="255" value="255" size="1">
    <Help>
This parameter will enable or disable the PIR detecting function.

Available settings: 0 or 255
0 – Disable PIR Detector Function
255 – Enable PIR Detector Function

Default setting: 255
    </Help>
        <Item label="Disable PIR Detection" value="0" />
        <Item label="Enable PIR Detection" value="255" />
        
  </Value>

<Value type="short" index="5" label="AMBIENT ILLUMINATION LUX LEVEL (LUX)" min="0" max="1000" value="100"  size="2">
<Help>
This parameter allows you to set a lux level value which determines when the light sensor is
activated. If the ambient illumination level falls below this value and a person moves
across or within the detected area , the PIR detector will send a Z-Wave ON
command(i.e. BASIC_SET value = parameter 3#) to an associated device and activate it.

Available settings: 0 - 1000
Default setting: 100 (lux)
</Help>
    </Value>
    
   
<Value type="byte" index="6" label="RE-TRIGGER INTERVAL SETTING" min="1" max="8" value="10">
    <Help>
This Parameter can be used to adjust the interval of being re-triggered after the PIR
detector has been triggered. No report will be sent during this interval if a movement
is detected. This Parameter value must be less than Parameter 2#.
Default setting: 8 (seconds)
Available settings: 1 - 8
    </Help>
</Value>

    <Value type="short" index="7" label="LIGHT SENSOR POLLING INTERVAL" min="60" max="36000" value="180" size="2">
<Help>
This Parameter can be used to set the interval time in seconds of the sensors ambient illumination level. (Seconds)

Available settings: 1 - 36000
Default setting: 180 (seconds)
</Help>
    </Value>

    <Value type="list" index="8" label="LUX LEVEL FUNCTION ENABLE" min="0" max="1" value="0" size="1">
<Help>
If this parameter is set to 1 then when Lux level is less than the value defined by Parameter no.5
the PIR sensor will send a BASIC_SET command frame (i.e. BASIC_SET [value = parameter 3]) to an associated device and activate it.
If Lux level is greater than the value defined by parameter 5 then the PIR sensor will not send a BASIC_SET command frame.
Default setting: 0 
Available settings: 0 OR 1
</Help>
<Item label="BASIC_SET command frame not sent" value="0" />
<Item label="BASIC_SET command frame sent." value="1" />

    </Value>
 
    <Value type="BYTE" index="9" label="AMBIENT ILLUMINATION LUX LEVEL REPORT (LUX)" min="0" max="255" value="100" >
<Help>
This parameter defines the amount, in lux, by which the Lux Level must change for it to be reported to the main controller.

Available settings: 0 - 255
Default setting: 100
</Help>
    </Value>
    
    <Value type="list" index="10" label="LED BLINK ENABLE" min="0" max="1" value="1" size="1">
    <Help>
This Parameter defines the LED On/Off enable. If this parameter is set to 1 then 
LED Blink will be enabled and the LED will blink once when motion is detected. Setting to 0 will disable the LED blink.
Available settings: 0 or 1
Default setting: 1 
    </Help>
       <Item label="Disable LED Blink" value="0" />
       <Item label="Enable LED Blink" value="1" /> 
  </Value>
   <Value type="short" index="99" label="AMBIENT LIGHT INTENSITY CALIBRATION" min="1" max="65535" value="1000" Size="2">
<Help>
This parameter defines the calibrated scale for ambient light intensity. Because the installation method and position of the sensor can result in illuminance errors this parameter allows user to adjust lux to a more accurate level.
Available settings: 1 - 65535
Default setting: 1000 

</Help>
    </Value>
</configuration>
'''
}
