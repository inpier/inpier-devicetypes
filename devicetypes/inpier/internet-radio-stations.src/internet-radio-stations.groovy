/**
 *  Copyright 2015 SmartThings
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
 *
 
 Version 2.1 2017/06/21 Updated to make the webCoRE Radio tile act as a mute button as suggested by tommyincville
 Version 2 2017/06/03 Updated to show which Radio Station Button is On.
 
 */
metadata {
	definition (name: "Internet Radio Stations  ", namespace: "inpier", author: "SmartThings/Inpier") {
		capability "Actuator"
        capability "Music Player"
        capability "Button"
		capability "Configuration"
		capability "Sensor"
        capability "Switch"
        capability "Refresh"
        
        attribute "buttonNumber","String"
        attribute "volumeUp", "String"
        attribute "volumeDown", "String"
        attribute "stationurl", "String"
        attribute "stationName", "String"
        attribute "setLevel", "string"
        attribute "mute", "string"
        //attribute "model", "string"
        attribute "level", "string"
		
        
        command "push1"
        command "push2"
        command "push3"
        command "push4"
        command "push5"
        command "push6"
        command "push7"
        command "push8"
        command "volup"
        command "voldown"
        command "stop"
        command "muter"
        command "unmuter"
		command "unsubscribe"
		command "setLocalLevel", ["number"]
		command "tileSetLevel", ["number"]
		
	}

/*	TO DO
simulator {
		
	}
*/
tiles {
		standardTile("button", "device.button0" , width: 2, height: 2, decoration: "flat") {
			state "nop", label: "webCoRE Radio Station Presets",  icon: "st.Electronics.electronics10", backgroundColor: "#ffffff"
            //state "on", label: '${currentValue}', icon: "st.Electronics.electronics10", backgroundColor: "#ffffff"
		}
 		
      
        valueTile(name:"stationName1", attribute:"stationName1", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push1"
        }
        standardTile("push1", "device.button1", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push1", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push1", icon: "st.alarm.beep.beep"
		}
 		valueTile(name:"stationName2", attribute:"stationName2", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push2"
        }
        standardTile("push2", "device.button2", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push2", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push2", icon: "st.alarm.beep.beep"
		}
 		valueTile(name:"stationName3", attribute:"stationName3", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}', action: "push3"
           //state "on", label: '${currentValue}', backgroundColor: "#ff9933"
        }
        standardTile("push3", "device.button3", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push3", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push3", icon: "st.alarm.beep.beep"
		}            
        valueTile(name:"stationName4", attribute:"stationName4", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push4"
        }
        standardTile("push4", "device.button4", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push4", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push4", icon: "st.alarm.beep.beep"
		}
 		
        standardTile("volup", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Vol +", backgroundColor: "#00b359", action: "volup", icon: ""
		}
 		valueTile(name:"stationName5", attribute:"stationName5", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push5"
        }
        standardTile("push5", "device.button5", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push5", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push5", icon: "st.alarm.beep.beep"
		}
 		valueTile(name:"stationName6", attribute:"stationName6", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push6"
        }
        standardTile("push6", "device.button6", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push6", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push6", icon: "st.alarm.beep.beep"
		}
 		
        standardTile("voldown", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Vol -", backgroundColor: "#00b359", action: "voldown", icon: ""
		}
 		valueTile(name:"stationName7", attribute:"stationName7", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push7"
        }
        standardTile("push7", "device.button7", width: 1, height: 1, decoration: "flat") {
			state "off", label:  "", backgroundColor: "#ffffff", action: "push7", icon: "st.Electronics.electronics16"
            state "on", label:  "Playing", backgroundColor: "#ff9933", action: "push7", icon: "st.alarm.beep.beep"
		}
 		valueTile(name:"stationName8", attribute:"stationName8", width: 2, height: 1, decoration: "flat", canChangeIcon: false) {
           state "nop", label: '${currentValue}',action: "push8"
        }
        standardTile("push8", "device.button8", width: 1, height: 1, decoration: "flat") {
			state "off", label: "", backgroundColor: "#ffffff", action: "push8", icon: "st.Electronics.electronics16"
            state "on", label: "Playing", backgroundColor: "#ff9933", action: "push8", icon: "st.alarm.beep.beep"
		}
 		standardTile("stop", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Stop", backgroundColor: "#ff3300", action: "stop"
		}
 		  standardTile("muter", "device.button9" , width: 1, height: 1, decoration: "flat") {
			state "on", label: "Mute", action: "muter", icon: "st.custom.sonos.unmuted", backgroundColor: "#0033cc"
            state "off", label: "Muted", action: "unmuter", icon: "st.custom.sonos.muted", backgroundColor: "#006600"
		}
        standardTile("blank", "blank", width: 2, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}'
		}
       valueTile ("volLevel","device.volLevel", width: 1, height: 1, decoration: "flat", canChangeIcon: false) {
				state"nop", label: 'Vol ${currentValue}'
			}
            controlTile("level","device.level", "slider", height: 1, width: 3, inactiveLabel: false, range:"(10..70)") {
			state"nop", label: '${currentValue}', action:"setLevel"
		}
        


	main "button"
	      //details(["button","stop","muter","volup","voldown","blank","stationName1","push1","stationName2","push2","stationName3","push3","stationName4","push4","stationName5","push5","stationName6","push6","stationName7","push7","stationName8","push8","level","volLevel"])
		details(["button","stop","level","muter","volLevel","blank","stationName1","push1","stationName2","push2","stationName3","push3","stationName4","push4","stationName5","push5","stationName6","push6","stationName7","push7","stationName8","push8"])

    }
preferences {

        section {
           input title: "Station Names", description: "Enter a Name and Url for each  Internet Radio Station in the fields below or leave as default.", displayDuringSetup: false, type: "paragraph", element: "paragraph"
           input name: "Station1", type: "text", title: "Enter Name for Station 1", description: "", defaultValue: "Station 1", displayDuringSetup: true
           input name: "Station1Url", type: "text", title: "Enter the Url for Station 1", description: "", defaultValue: "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p", displayDuringSetup: false
           input name: "Station2", type: "text", title: "Enter Name for Station 2", description: "", defaultValue: "Station 2", displayDuringSetup: false
		   input name: "Station2Url", type: "text", title: "Enter the Url for Station 2", description: "", defaultValue: "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio4fm_mf_p", displayDuringSetup: false
           input name: "Station3", type: "text", title: "Enter Name for Station 3", description: "", defaultValue: "Station 3", displayDuringSetup: false
           input name: "Station3Url", type: "text", title: "Enter the Url for Station 3", description: "", defaultValue: "http://player.absoluteradio.co.uk/tunein.php?i=a764.aac", displayDuringSetup: false
           input name: "Station4", type: "text", title: "Enter Name for Station 4", description: "", defaultValue: "Station 4", displayDuringSetup: false
           input name: "Station4Url", type: "text", title: "Enter the Url for Station 4", description: "", defaultValue: "http://vis.media-ice.musicradio.com/CapitalUK", displayDuringSetup: false
           input name: "Station5", type: "text", title: "Enter Name for Station 5", description: "", defaultValue: "Station 5", displayDuringSetup: false
           input name: "Station5Url", type: "text", title: "Enter the Url for Station 5", description: "", defaultValue: "http://media-ice.musicradio.com/SmoothLondon", displayDuringSetup: false
           input name: "Station6", type: "text", title: "Enter Name for Station 6", description: "", defaultValue: "Station 6", displayDuringSetup: false
           input name: "Station6Url", type: "text", title: "Enter the Url for Station 6", description: "", defaultValue: "http://media-ice.musicradio.com/HeartLondon", displayDuringSetup: false
           input name: "Station7", type: "text", title: "Enter Name for Station 7", description: "", defaultValue: "Station 7", displayDuringSetup: false
		   input name: "Station7Url", type: "text", title: "Enter the Url for Station 7", description: "", defaultValue: "http://player.absoluteradio.co.uk/tunein.php?i=a664.aac", displayDuringSetup: false
           input name: "Station8", type: "text", title: "Enter Name for Station 8", description: "", defaultValue: "Station 8", displayDuringSetup: false
           input name: "Station8Url", type: "text", title: "Enter the Url for Station 8", description: "", defaultValue: "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio1_mf_p", displayDuringSetup: false
        }
    }
}

def parse(String description) {
		log.debug "Parsing '${description}'"
}

def push1() {
    change()
    sendEvent(name: "button1", value: "on")
    sendEvent(name:"stationName1", value: "$Station1")
    sendEvent(name:"blank", value: "Playing $Station1")
    state.station = "button1"
    state.stationName ="$Station1"
    state.stationNumber = "1"
    play()
    push(1)
    pushurl("$Station1Url")
    pushname("$Station1")
}

def push2() {
    change()
    sendEvent(name: "button2", value: "on")
    sendEvent(name:"stationName2", value: "$Station2")
    sendEvent(name:"blank", value: "Playing $Station2")
    state.station = "button2"
    state.stationNumber = "2"
    state.stationName ="$Station2"
    play()
    push(2)
    pushurl("$Station2Url")
    pushname("$Station2")
}

def push3() {
    change()
    sendEvent(name: "button3", value: "on") 
    sendEvent(name:"stationName3", value: "$Station3")
    sendEvent(name:"blank", value: "Playing $Station3")  
    state.station = "button3"
    state.stationName = "$Station3"
    state.stationNumber = "3"
    play()
    push(3)
    pushurl("$Station3Url")
    pushname("$Station3")
    log.debug "$state.stationName"
}

def push4() {
    change()
    sendEvent(name: "button4", value: "on")
	sendEvent(name:"stationName4", value: "$Station4")
    sendEvent(name:"blank", value: "Playing $Station4")
    state.station = "button4"
    state.stationName ="$Station4"
    state.stationNumber = "4"
    play()
    push(4)
    pushurl("$Station4Url")
    pushname("$Station4")
}

def push5() {
    change()
    sendEvent(name: "button5", value: "on")
	sendEvent(name:"stationName5", value: "$Station5")
    sendEvent(name:"blank", value: "Playing $Station5")
    state.station = "button5"
    state.stationName ="$Station5"
    state.stationNumber = "5"
    play()
    push(5)
    pushurl("$Station5Url")
    pushname("$Station5")
}

def push6() {
    change()
    sendEvent(name: "button6", value: "on")    
	sendEvent(name:"stationName6", value: "$Station6")
    sendEvent(name:"blank", value: "Playing $Station6")
    state.station = "button6"
    state.stationName ="$Station6"
    state.stationNumber = "6"
    play()
    push(6)
    pushurl("$Station6Url")
    pushname("$Station6")
}

def push7() {
    change() 
    sendEvent(name: "button7", value: "on" ) 
    sendEvent(name:"stationName7", value: "$Station7")
    sendEvent(name:"blank", value: "Playing $Station7")
    state.station = "button7"
    state.stationName ="$Station7"
    state.stationNumber = "7"
    play()
    push(7)
    pushurl("$Station7Url")
    pushname("$Station7")
}

def push8() {
    change()
    sendEvent(name: "button8", value: "on")    
	sendEvent(name:"stationName8", value: "$Station8")
    sendEvent(name:"blank", value: "Playing $Station8")
    state.station = "button8"
    state.stationName ="$Station8"
    state.stationNumber = "8"
    play()
    push(8)
    pushurl("$Station8Url")
    pushname("$Station8")
}

def change() { 
  sendEvent(name: "button0", value: "off")
  sendEvent(name: "button1", value: "off")
  sendEvent(name: "button2", value: "off")
  sendEvent(name: "button3", value: "off")
  sendEvent(name: "button4", value: "off")
  sendEvent(name: "button5", value: "off")
  sendEvent(name: "button6", value: "off")
  sendEvent(name: "button7", value: "off")
  sendEvent(name: "button8", value: "off")
  sendEvent(name: "button9", value: "on")
}


def stop() {
    change()
    sendEvent(name:"blank", value: "No Station Playing")
    //sendEvent(name: "status", value: "stopped")
    //sendEvent(name: "switch", value: "off")
    pause()
    //push(9)
}
/*
def volup() {
	sendEvent(name: "button9", value: "on")
    push(10)
}

def voldown() {
	sendEvent(name: "button9", value: "on")
    push(11)
}
*/
def muter() {
	sendEvent(name: "button9", value: "off") 
    sendEvent(name: "mute", value: "muted")
    push(12)
}

def unmuter() {
	sendEvent(name: "button9", value: "on") 
    sendEvent(name: "mute", value: "unmuted")
    push(12)
}
def setLevel(val) {
    log.debug "Executing 'setLevel'"
	sendEvent(name: "level", value: "$val")
    sendEvent(name: "volLevel", value: "$val")
    sendEvent(name: "setLevel", value: "$val")
    //sendEvent(name: "play", value: "$val")
}

def tileSetLevel(val) {
	log.debug "Executing 'tileSetLevel' Volume $val"
	//sendEvent(name: "switch", value: "on")"
    sendEvent(name: "setLocalLevel", value: "$val")
    sendEvent(name: "setLevel", value: "$val")
    sendEvent(name: "level", value: "$val")
   
}

def play() {
	log.debug "Executing 'play'"
	sendEvent(name: "$state.station", value: "on")
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "status", value: "playing")
    sendEvent(name: "trackDescription", value: "$state.stationName")
    sendEvent(name: "blank", value: "Playing $state.stationName")
    log.debug "$state.station"
}

def pause() {
	log.debug "Executing 'pause'"
	sendEvent(name: "$state.station", value: "off")
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "status", value: "paused")
    sendEvent(name: "trackDescription", value: "$state.stationName")
    sendEvent(name:"blank", value: "No Station Playing")
}

def nextTrack() {
	//log.debug "$state.station".substring(6)
    state.stationNo = "$state.station".substring(6).toInteger() +1
    //log.debug "$state.stationNo"
   log.debug "$state.stationNo"
    if ("$state.stationNo" > 8) {
    state.stationNo = 1
    }
   changeStation("$state.stationNo")
    
}
 
def previousTrack() {
	//log.debug "$state.station".substring(6)
    state.stationNo = "$state.station".substring(6).toInteger() -1
    //$state.stationNo = toInteger("$state.stationNo")
    //state.station = "button$state.stationNo"
    log.debug "$state.stationNo"
    
    if ("$state.stationNo" < 1) {
    state.stationNo = 8
    }
    
    changeStation("$state.stationNo")
    //log.debug "$state.stationNo"
	//log.debug "$state.station"
}


def mute() {
	log.debug "Executing 'mute'"
	sendEvent(name: "mute", value: "muted")
    //sendEvent(name: "playerMute", value: "muted")
    sendEvent(name: "button9", value: "off")
    push(12)
}

def unmute() {
	log.debug "Executing 'unmute'"
	sendEvent(name: "mute", value: "unmuted")
    //sendEvent(name: "playerMute", value: "unmuted")
    sendEvent(name: "button9", value: "on")
    push(12)
}
private changeStation(stationChange){
	log.debug "Changing Station"
    switch("$stationChange") {
    	case "1":
        	push1()
	        break;
       	case "2":
        	push2()
	        break;
        case "3":
        	push3()
	        break;
        case "4":
        	push4()
	        break;
       	case "5":
        	push5()
  	        break;
       	case "6":
        	push6()
  	        break;
       	case "7":
        	push7()
  	        break;
       	case "8":
        	push8()
  	        break;    
        default:
        	log.warn "Received unexpected Station Number"
    }
    
}
private push(button) {
	log.debug "$device.displayName button $button was pushed"
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
    sendEvent(name: "buttonNumber", value: "$button",  descriptionText: "", isStateChange: true)
   
}

private pushurl(url) {
	//log.debug "$device.displayName button $button was held"
	//sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was held", isStateChange: true)
     sendEvent(name: "stationurl", value: "$url",  descriptionText: "", isStateChange: true)
     //sendEvent(name: "buttonNumber", value: "$button",  descriptionText: "", isStateChange: true)
}
private pushname(stationname) {
	//log.debug "$device.displayName button $button was pushed"
    sendEvent(name: "stationName", value: "$stationname",  descriptionText: "", isStateChange: true)
   
}

def installed() {
	log.debug "Installing"
    initialize()
}

def updated() {
	//change()
    sendEvent(name:"stationName1", value: "$Station1")
    sendEvent(name:"stationName2", value: "$Station2")
    sendEvent(name:"stationName3", value: "$Station3")
    sendEvent(name:"stationName4", value: "$Station4")
    sendEvent(name:"stationName5", value: "$Station5")
    sendEvent(name:"stationName6", value: "$Station6")
    sendEvent(name:"stationName7", value: "$Station7")
    sendEvent(name:"stationName8", value: "$Station8")
    //sendEvent(name: "trackDescription", value: "$state.stationName")
    log.debug "Updating"
    
    initialize()
}

def initialize() {
	//log.debug "Initializing"
    sendEvent(name: "numberOfButtons", value: 12)
   
}