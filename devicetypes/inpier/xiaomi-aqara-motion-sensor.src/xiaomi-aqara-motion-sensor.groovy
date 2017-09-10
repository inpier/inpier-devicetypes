/**
 *  Xiaomi Aqara Motion Sensor
 *
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
 * mods made (to bspranger's fork)  by inpier on 05/09/2017 to introduce lux reading. NOTE This only updates during a motion event as 
 * the device does not appear to acccept the configureReporting command for illuminance. 
 *
 * Based on original DH by Eric Maycock 2015
 * modified 29/12/2016 a4refillpad 
 * Added fingerprinting
 * Added heartbeat/lastcheckin for monitoring
 * Added battery and refresh 
 * Motion background colours consistent with latest DH
 * Fixed max battery percentage to be 100%
 * Added Last update to main tile
 * Added last motion tile
 * Heartdeat icon plus improved localisation of date
 * removed non working tiles and changed layout and incorporated latest colours
 * added experimental health check as worked out by rolled54.Why
 *
 */

metadata {
	definition (name: "Xiaomi Aqara Motion Sensor", namespace: "inpier", author: "a4refillpad") {
		capability "Motion Sensor"
		capability "Configuration"
		capability "Battery"
		capability "Sensor"
		capability "Refresh"
        capability "Health Check"
        capability "Illuminance Measurement"
        
        attribute "lastCheckin", "String"
        attribute "lastMotion", "String"
        //attribute "illuminance", "String"

    	fingerprint profileId: "0104", deviceId: "0104", inClusters: "0000, 0003, 0001, 0400, FFFF, 0019", outClusters: "0000, 0004, 0003, 0006, 0008, 0005, 0019", manufacturer: "LUMI", model: "lumi.sensor_motion.aq2", deviceJoinName: "Xiaomi Aqara Motion"
       //fingerprint profield: "0104", inClusters: "0000, 0400, 0406, FFFF", outClusters: "0000, 0019", manufacturer: "LUMI", model: "lumi.sensor_motion.aq2", deviceJoinName: "Xiaomi Aquara Motion"
 
        command "reset"
        command "Refresh"
        
	}

	simulator {
	}

	preferences {
		input "motionReset", "number", title: "Number of seconds after the last reported activity to report that motion is inactive (in seconds). \n\n(The device will always remain blind to motion for 60seconds following first detected motion. This value just clears the 'active' status after the number of seconds you set here but the device will still remain blind for 60seconds in normal operation.)", description: "", value:120, displayDuringSetup: false
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"motion", type: "generic", width: 6, height: 4){
			tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {
				attributeState "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#00a0dc"
				attributeState "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}
              
	    standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("configure", "device.configure", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
	    }       
        
		standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"reset", label: "Reset Motion"
		}
		standardTile("icon", "device.refresh", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
            state "default", label:'Last Motion:', icon:"st.Entertainment.entertainment15"
        }
        valueTile("lastmotion", "device.lastMotion", decoration: "flat", inactiveLabel: false, width: 4, height: 1) {
			state "default", label:'${currentValue}'
        }
        standardTile("refresh", "command.refresh", inactiveLabel: false) {
			state "default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
	   }
		valueTile("illuminance", "device.illuminance", width: 2, height: 2) {
			state "illuminance", label:'${currentValue}\nlux', unit:"lux"
            
        }
		main(["motion"])
		details(["motion", "battery", "illuminance", "icon", "lastmotion", "reset", "refresh"])
	}
}

def parse(String description) {
	def linkText = getLinkText(device)
    log.debug "${linkText} Parsing: $description"

  
	Map map = [:]
	if (description?.startsWith('catchall:')) {
		map = parseCatchAllMessage(description)
	}
	else if (description?.startsWith('read attr -')) {
		map = parseReportAttributeMessage(description)
	}
    else if (description?.startsWith('illuminance')) {
			//getLumminanceResult(description)
            map = parseCustomMessage(description)
	}
	log.debug "${linkText} Parse returned: $map"
	def result = map ? createEvent(map) : null
//  send event for heartbeat    
    def now = new Date().format("yyyy MMM dd EEE h:mm:ss a", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
    
    if (description?.startsWith('enroll request')) {
    	List cmds = enrollResponse()
        log.debug "${linkText} enroll response: ${cmds}"
        result = cmds?.collect { new physicalgraph.device.HubAction(it) }
    }
     log.debug "Parse returned $map"
       return result
}

private Map getLuminanceResult(rawValue) {
	log.debug "Luminance rawValue = ${rawValue}"

	def result = [
		name: 'illuminance',
		value: '--',
		translatable: true,
 		unit: 'lux'
	]
    
    result.value = rawValue as Integer
    return result
}

/* Old method 
private getLumminanceResult(String luxLevel) {
	
	//log.debug luxLevel.substring(12)
	def lux = luxLevel.substring(12)
    def linkText = getLinkText(device)
	log.debug "${linkText} Illuminance is  ${lux} lux"
    sendEvent(name: "illuminance", value: "$lux")
    //sendEvent(name: "Illuminance Measurement", value: "$lux")
	return
}
*/
private Map getBatteryResult(rawValue) {
	//log.debug 'Battery'
	def linkText = getLinkText(device)

	//log.debug rawValue

	def result = [
		name: 'battery',
		value: '--'
	]
    
	def volts = rawValue / 1
    
    def maxVolts = 100

	if (volts > maxVolts) {
				volts = maxVolts
    }

    result.value = volts
	result.descriptionText = "${linkText} battery was ${result.value}%"

	return result
}

private Map parseCatchAllMessage(String description) {
    def linkText = getLinkText(device)
    
	Map resultMap = [:]
	def cluster = zigbee.parse(description)
	log.debug cluster
	if (shouldProcessMessage(cluster)) {
		switch(cluster.clusterId) {
			case 0x0000:
			resultMap = getBatteryResult(cluster.data.get(30))
			break
//#############################################################
			case 0x0001:
				// 0x07 - configure reporting
				if (cluster.command != 0x07) {
					resultMap = getBatteryResult(cluster.data.last())
				}
			break

			case 0x0400:
            	if (cluster.command == 0x07) { // Ignore Configure Reporting Response
                	if(cluster.data[0] == 0x00) {
						log.trace "Luminance Reporting Configured"
						sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
					}
					else {
						log.warn "Luminance REPORTING CONFIG FAILED- error code:${cluster.data[0]}"
					}
				}
				else {
            		log.debug "catchall : luminance" + cluster
                	resultMap = getLuminanceResult(cluster.data.last());
                }

			break
//#########################################################################
			case 0xFC02:
			log.debug '${linkText}: ACCELERATION'
			break

			case 0x0402:
			log.debug '${linkText}: TEMP'
				// temp is last 2 data values. reverse to swap endian
				String temp = cluster.data[-2..-1].reverse().collect { cluster.hex1(it) }.join()
				def value = getTemperature(temp)
				resultMap = getTemperatureResult(value)
				break
		}
	}

	return resultMap
}

private boolean shouldProcessMessage(cluster) {
	// 0x0B is default response indicating message got through
	// 0x07 is bind message
	boolean ignoredMessage = cluster.profileId != 0x0104 ||
	cluster.command == 0x0B ||
	cluster.command == 0x07 ||
	(cluster.data.size() > 0 && cluster.data.first() == 0x3e)
	return !ignoredMessage
}

def configure() {

// TODO : device watch?

	String zigbeeId = swapEndianHex(device.hub.zigbeeId)
	log.debug "Confuguring Reporting and Bindings."
    
    
	def configCmds = []
    configCmds += zigbee.batteryConfig()
	   
    //configCmds += zigbee.configureReporting(0x406,0x0000, 0x18, 30, 600, null) // motion // confirmed
        
    // Data type is not 0x20 = 0x8D invalid data type Unsigned 8-bit integer
    
	configCmds += zigbee.configureReporting(0x400,0x0000, 0x21, 0, 600, 0x20) // Set luminance reporting times?? maybe  Not working  
    return refresh() + configCmds 
}



def enrollResponse() {
    def linkText = getLinkText(device)
    log.debug "${linkText}: Enrolling device into the IAS Zone"
	[
			// Enrolling device into the IAS Zone
			"raw 0x500 {01 23 00 00 00}", "delay 200",
			"send 0x${device.deviceNetworkId} 1 1"
	]
}

def refresh() {
    log.debug "Refreshing Values"

    def refreshCmds = []
    refreshCmds +=zigbee.readAttribute(0x0001, 0x0020) // Read battery?
    //refreshCmds += zigbee.readAttribute(0x0402, 0x0000) // Read temp?
    refreshCmds += zigbee.readAttribute(0x0400, 0x0000) // Read luminance?
    //refreshCmds += zigbee.readAttribute(0x0406, 0x0000) // Read motion?
	//log.debug refreshCmds
    return refreshCmds + enrollResponse()

    }


private Map parseReportAttributeMessage(String description) {
	Map descMap = (description - "read attr - ").split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
	//log.debug "Desc Map: $descMap"
 
	Map resultMap = [:]
    def now = new Date().format("yyyy MMM dd EEE h:mm:ss a", location.timeZone)
    
   if (descMap.cluster == "0000" && descMap.attrId == "0005") {
		log.info "Cluster 0000 and AttrID 0005"
        //def luxValue = descMap.value.substring(30..31)
        //resultMap = getLuminanceResult(Integer.parseInt(luxValue, 16))
     
	}
	if (descMap.cluster == "0001" && descMap.attrId == "0020") {
		resultMap = getBatteryResult(Integer.parseInt(descMap.value, 16))
	}
     // Luminance
    else if (descMap.cluster == "0400" ) { //&& descMap.attrId == "0020") {
		log.error "Luminance Response " + description
        //result << getBatteryResult(Integer.parseInt(descMap.value, 16))
	}

    else if (descMap.cluster == "0406" && descMap.attrId == "0000") {
    	def value = descMap.value.endsWith("01") ? "active" : "inactive"
	    sendEvent(name: "lastMotion", value: now)
        if (settings.motionReset == null || settings.motionReset == "" ) settings.motionReset = 120
        if (value == "active") runIn(settings.motionReset, stopMotion)
    	resultMap = getMotionResult(value)
    } 
	return resultMap
}
 
private Map parseCustomMessage(String description) {
	Map resultMap = [:]
//###############################################################################################
if (description?.startsWith('illuminance: ')) {
   // log.info "value: " + description.split(": ")[1]
            //log.warn "proc: " + value

		//def value = zigbee.lux( description.split(": ")[1] as Integer ) //zigbee.parseHAIlluminanceValue(description, "illuminance: ", getTemperatureScale())
		def value = description.split(": ")[1]
        resultMap = getLuminanceResult(value)
	}
//###############################################################################################
	return resultMap
}

private Map parseIasMessage(String description) {
    def linkText = getLinkText(device)
    List parsedMsg = description.split(' ')
    String msgCode = parsedMsg[2]
    
    Map resultMap = [:]
    switch(msgCode) {
        case '0x0020': // Closed/No Motion/Dry
        	resultMap = getMotionResult('inactive')
            break

        case '0x0021': // Open/Motion/Wet
        	resultMap = getMotionResult('active')
            break

        case '0x0022': // Tamper Alarm
        	log.debug '${linkText}: motion with tamper alarm'
        	resultMap = getMotionResult('active')
            break

        case '0x0023': // Battery Alarm
            break

        case '0x0024': // Supervision Report
        	log.debug '${linkText}: no motion with tamper alarm'
        	resultMap = getMotionResult('inactive')
            break

        case '0x0025': // Restore Report
            break

        case '0x0026': // Trouble/Failure
        	log.debug '${linkText}: motion with failure alarm'
        	resultMap = getMotionResult('active')
            break

        case '0x0028': // Test Mode
            break
    }
    return resultMap
}


private Map getMotionResult(value) {
	def linkText = getLinkText(device)
    //log.debug "${linkText}: motion"
	String descriptionText = value == 'active' ? "${linkText} detected motion" : "${linkText} motion has stopped"
	def commands = [
		name: 'motion',
		value: value,
		descriptionText: descriptionText
	] 
    return commands
}

private byte[] reverseArray(byte[] array) {
    byte tmp;
    tmp = array[1];
    array[1] = array[0];
    array[0] = tmp;
    return array
}

private String swapEndianHex(String hex) {
    reverseArray(hex.decodeHex()).encodeHex()
}

def stopMotion() {
   sendEvent(name:"motion", value:"inactive")
}

def reset() {
	sendEvent(name:"motion", value:"inactive")
}

def installed() {
// Device wakes up every 1 hour, this interval allows us to miss one wakeup notification before marking offline
	def linkText = getLinkText(device)
    log.debug "${linkText}: Configured health checkInterval when installed()"
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def updated() {
// Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline
	def linkText = getLinkText(device)
    log.debug "${linkText}: Configured health checkInterval when updated()"
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}