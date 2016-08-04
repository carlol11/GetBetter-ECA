from VH import *
import math

import sys
sys.path.append("/sdcard/vhdata/pythonlibs")
print sys.modules.keys()

print "hola"

characterName = "ChrLindsay"
bmlID = 0

def saySomething(actor, message):
	bmlMsg = androidEngine.getNonverbalBehavior(message)
	print "bmlMsg: " + bmlMsg
	stopTalking(actor)
	bmlID = bml.execXML(actor, bmlMsg)

def doBehavior(actor, message):
	bmlMsg = androidEngine.getBehavior(message, 0, 5)
	bml.execBML(actor, bmlMsg)
	print bmlMsg

def stopTalking(actor):
	bml.interruptCharacter(actor, bmlID)
	print "interrupt talking"

class MyVHEngine(VHEngine):
	def eventInit(self):
		print "Starting VH engine..."		
		self.initTextToSpeechEngine('cerevoice') # initialize tts engine for speech synthesis
	
	def eventInitUI(self):	
		self.setBackgroundImage('/sdcard/vhdata/office2.png')
				
androidEngine = MyVHEngine()
setVHEngine(androidEngine)		
	
scene.addAssetPath("script", "scripts")
scene.addAssetPath("motion", "characters")
scene.setBoolAttribute("internalAudio", True)
scene.run("camera.py")
scene.run("light.py")
scene.run("setupCharacter.py")
scene.run("init-emotions.py")
setupCharacter(characterName, characterName, "", "")
character = scene.getCharacter(characterName)
bml.execBML(characterName, '<body posture="ChrBrad@Idle01"/>')
bml.execBML(characterName, '<saccade mode="listen"/>')
sim.start()

# start the first utterance
#scene.command('bml char ChrRachel file ./Sounds/intro_age_1.xml')
