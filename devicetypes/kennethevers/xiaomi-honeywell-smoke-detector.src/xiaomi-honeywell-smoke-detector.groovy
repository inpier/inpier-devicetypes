/**
 *  Xiaomi Honeyweel Zigbee Smoke Detector. BETA version in very basic code that just reads SMOKE or CLEAR states at the moment
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
 *  2017/09/11 -- Inpier added Battery Level and Smoke Alarm Last Tested. Note both these are best guess from live logging data received.
 *  			  PDF Manual can be found here - http://files.xiaomi-mi.com/files/MiJia_Honeywell/MiJia_Honeywell_Smoke_Detector_EN.pdf
 *
 *
 *
 */
 
metadata {
	definition (name: "Xiaomi Honeywell Smoke Detector", namespace: "KennethEvers", author: "Kenneth Evers") {
		
        capability "Configuration"
        capability "Smoke Detector"
        capability "Sensor"
        capability "Battery"  // NOT WORKING #### Fixed??
        //capability "Temperature Measurement" //attributes: temperature NOT WORKING 
        capability "Refresh"
        
        command "enrollResponse"
        attribute "lastTested", "String"
 
 
		fingerprint profileID: "0104", deviceID: "0402", inClusters: "0000,0003,0012,0500", outClusters: "0019"
        
}
 
	simulator {
 
	}

	preferences {}
 
	tiles {
		multiAttributeTile(name:"smoke", type: "generic", width: 6, height: 4) {
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
           		attributeState("clear", label:'CLEAR', icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff")
            	attributeState("detected", label:'SMOKE', icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")   
 			}
		}
        
        valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "battery", label:'${currentValue}% battery', unit:"%"
        } // NOT WORKING
        
        standardTile("icon", "", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'Last Tested', icon:"st.alarm.smoke.test"
        }
        valueTile("lastTested", "device.lastTested", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
            state "default", label:'${currentValue}'//, backgroundColor:"#33cc33"
            //state "notTested", label:'Not Tested since: ${currentValue}', backgroundColor:"#ff3300"
        } // NOT WORKING
            
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"configuration.configure", icon:"st.secondary.configure"
		} 		
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}        
		main (["smoke"])
		details(["smoke", "battery", "refresh","configure", "icon", "lastTested"])
	}
}
 
//def updated() {
 //   log.debug "Updated with settings: ${settings}"
 //   setConfigured("false") //wait until the next time device wakeup to send configure command
//}


def parse(String description) {
	log.debug "description: $description"
    
	Map map = [:]
	if (description?.startsWith('catchall:')) {
		map = parseCatchAllMessage(description)
	}
	else if (description?.startsWith('read attr -')) {
		map = parseReportAttributeMessage(description)
	}
    else if (description?.startsWith('zone status')) {
    	map = parseIasMessage(description)
    }
 
	log.debug "Parse returned $map"
	def result = map ? createEvent(map) : null
    
    if (description?.startsWith('enroll request')) {
    	List cmds = enrollResponse()
        log.debug "enroll response: ${cmds}"
        result = cmds?.collect { new physicalgraph.device.HubAction(it) }
    }
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
		}
	}

	return resultMap
}

/*
private Map parseCatchAllMessage(String description) {
    Map resultMap = [:]
    def cluster = zigbee.parse(description)
    if (shouldProcessMessage(cluster)) {
        log.debug "Parse $cluster"
    }

    return resultMap
}
*/
private boolean shouldProcessMessage(cluster) {
    // 0x0B is default response indicating message got through
    // 0x07 is bind message
    boolean ignoredMessage = cluster.profileId != 0x0104 || 
        cluster.command == 0x0B ||
        cluster.command == 0x07 ||
        (cluster.data.size() > 0 && cluster.data.first() == 0x3e)
    return !ignoredMessage
}

 
private Map parseReportAttributeMessage(String description) {
	Map descMap = (description - "read attr - ").split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
	log.debug "Desc Map: $descMap"
 
}
 


private Map parseIasMessage(String description) {
    List parsedMsg = description.split(' ')
    String msgCode = parsedMsg[2]
    
    Map resultMap = [:]
    switch(msgCode) {
        case '0x0000': // Clear 
        	resultMap = getSmokeResult('clear')
            break
            
        case '0x0001': // Smoke 
        	resultMap = getSmokeResult('detected')
            break
        case '0x0002': // Test 
        	resultMap = getTestedResult('tested')
            //log.debug "TESTED"
            break
        case 'default':
        log.debug "IAS Message default $msgCode"
        break
    }
    return resultMap
}


private Map getSmokeResult(value) {
	log.debug 'Smoke Status' 
	def linkText = getLinkText(device)
	def descriptionText = "${linkText} is ${value == 'detected' ? 'detected' : 'clear'}"
	return [
		name: 'smoke',
		value: value,
		descriptionText: descriptionText
	]
}

private Map getTestedResult(value) {
	def now = new Date().format("yyyy MMM dd EEE h:mm:ss a", location.timeZone)
    log.debug 'Test Status' 
	def linkText = getLinkText(device)
	def descriptionText = "${linkText} is ${value = 'Tested'}"
	return [
		name: 'lastTested',
		value: now,
		descriptionText: descriptionText
	]
}
// DOESNT WORK PROPERLY NEEDS TESTING AND ADJUSTING
def refresh() {		//read enrolled state and zone type from IAS cluster
	[
	    "st rattr 0x${device.deviceNetworkId} ${endpointId} 0x0500 0", "delay 500",
        "st rattr 0x${device.deviceNetworkId} ${endpointId} 0x0500 1"
	]
    log.debug "refreshing"
}	
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

def configure() {

	String zigbeeId = swapEndianHex(device.hub.zigbeeId)
	log.debug "Confuguring Reporting, IAS CIE, and Bindings."
	def configCmds = [
		"zcl global write 0x500 0x10 0xf0 {${zigbeeId}}", "delay 200",
		"send 0x${device.deviceNetworkId} 1 ${endpointId}", "delay 1500",
        
        // DOESNT WORK PROPERLY NEEDS TESTING AND ADJUSTING
        //"raw 0x500 {01 23 00 00 00}", "delay 200",
        //"send 0x${device.deviceNetworkId} 1 1", "delay 1500",
        
	]
    log.debug "configure: Write IAS CIE"
    return configCmds // send refresh cmds as part of config
}

def enrollResponse() {
	log.debug "Sending enroll response"
    [	
    // DOESNT WORK PROPERLY NEEDS TESTING AND ADJUSTING	
	"raw 0x500 {01 23 00 00 00}", "delay 200",
        "send 0x${device.deviceNetworkId} 1 ${endpointId}"
        
    ]
}
private hex(value) {
	new BigInteger(Math.round(value).toString()).toString(16)
}

private String swapEndianHex(String hex) {
    reverseArray(hex.decodeHex()).encodeHex()
}

private byte[] reverseArray(byte[] array) {
    int i = 0;
    int j = array.length - 1;
    byte tmp;
    while (j > i) {
        tmp = array[j];
        array[j] = array[i];
        array[i] = tmp;
        j--;
        i++;
    }
    return array
}