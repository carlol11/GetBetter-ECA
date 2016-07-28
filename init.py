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
	print bmlMsg
	stopTalking(actor)
	bmlID = bml.execXML(actor, bmlMsg)

def doBehavior(actor, message):
	bmlMsg = androidEngine.getBehavior(message, 0, 5)
	bml.execXML(actor, bmlMsg)

def stopTalking(actor):
	bml.interruptCharacter(actor, bmlID)
	print "interrupt talking"

class MyVHEngine(VHEngine):
	currQuestion = 0
	questions = (["Hi, I am Geebee! Are you ready?",
		"Are you a boy or a girl?",
		"Are you right-handed or left-handed?",
		"Do you have a fever?",
		"Are you experiencing any pain?",
		"Do you have any injuries?",
		"Are you experiencing general unwellness?",
		"Do you have sore throat?",
		"Move 5.65 meters away, cover your right eye and begin reading the letters.",
		"What shape can you see? Choose the one that resembles the shape that you see.",
		"When you hear the sound press the button.",
		"Run in place until the music stops.",
		"Do jumping Jacks until the music stops.",
		"Hop in place until the music stops.",
		"Using a finger of your left hand. Trace the path, help the lion go to his friends."])
	isNotDoneHearing = True

	def eventInit(self):
		print "Starting VH engine..."		
		self.initTextToSpeechEngine('cerevoice') # initialize tts engine for speech synthesis
	
	def eventInitUI(self):	
		self.setWidgetProperty('button1',1, "start")
		self.setWidgetProperty('button2',-1, 'Speak 2')
		self.setWidgetProperty('button3',-1, 'Speak 3')
		self.setWidgetProperty('button4',-1, 'Speak 4')
		self.setWidgetProperty('speak_button',-1, 'Press To Speak')
		self.setWidgetProperty('exit_button',-1, 'Exit button')
		
		self.setBackgroundImage('/sdcard/vhdata/office2.png')
		self.setWidgetProperty('topic_text',1, 'Geebee')
		self.setWidgetProperty('program_text',1, 'Geebee') 

	
	def eventButtonTouch(self, btnName, btnAction):
		# if btnName == 'button1':
		# 	if btnAction == 0: # ACTION_DOWN
		# 		print "BUTTON DOWN"
		# 		if self.isVoiceRecognitionListening():
		# 			print "Voice recognition already active, not starting another one."
		# 		else:
		# 			print 'startVoiceRecognition'
		# 			self.startVoiceRecognition()
		# 			self.setWidgetProperty('button1',1, 'Release to Stop')
		# 			bml.execBML('ChrRachel', self.getBehavior("half_nod", 2, 4))
		# 	elif btnAction == 1: # ACTION_UP
		# 		print "BUTTON Up"
		# 		if not self.isVoiceRecognitionListening():
		# 			print "Voice recognition is not already active, ignoring stop voice recognition request..."
		# 		else:
		# 			print 'stopVoiceRecognition'
		# 			self.stopVoiceRecognition()
		# 			self.setWidgetProperty('button1',1, 'Press and hold To Speak')

		saySomething(characterName, "Let\'s find how well you can hear.")

		# currQuestion = self.currQuestion
		# questions = self.questions
		# isNotDoneHearing = self.isNotDoneHearing
		# if currQuestion == 0 and btnAction == 1:
		# 	saySomething(characterName, questions[currQuestion])
		# 	doBehavior(characterName, 'emo-joy')
		# 	currQuestion+=1
		# 	self.setWidgetProperty('button1',1, "Let's go")
		
		# elif currQuestion == 1 and btnAction == 1: 
		# 	saySomething(characterName, questions[currQuestion])

		# 	self.setWidgetProperty('button1',1, 'Boy')
		# 	self.setWidgetProperty('button2',1, 'Girl')

		# 	currQuestion = currQuestion + 1 
		# elif currQuestion == 2 and btnAction == 1:
		# 	saySomething(characterName, questions[currQuestion])

		# 	self.setWidgetProperty('button1',1, 'Right-handed')
		# 	self.setWidgetProperty('button2',1, 'Left-handed')
		# 	currQuestion+=1

		# elif (currQuestion == 3 or currQuestion == 4 or currQuestion == 5 or currQuestion == 6 or currQuestion == 7) and btnAction == 1: 
		# 	if currQuestion ==3:
		# 		self.setWidgetProperty('button1',1, 'Yes')
		# 		self.setWidgetProperty('button2',1, 'No')
		# 	saySomething(characterName, questions[currQuestion])
		# 	currQuestion+=1

		# elif (currQuestion == 8 or currQuestion == 9) and btnAction == 1:
		# 	saySomething(characterName, questions[currQuestion])
		# 	currQuestion+=1
		# 	if currQuestion == 9:
		# 		self.setWidgetProperty('button1',1, 'Done')
		# 		self.setWidgetProperty('button2',-1, 'None')

		# elif currQuestion == 10 and btnAction == 1:
		# 	if isNotDoneHearing:
		# 		saySomething(characterName, questions[currQuestion])
		# 		self.setWidgetProperty('button1',1, 'Press me')
		# 		self.setWidgetProperty('button4',1, 'Done')
		# 		isNotDoneHearing = False
		# 	if btnName == 'button4':
		# 		currQuestion+=1
		# 		self.setWidgetProperty('button1',1, 'Done')
		# 		self.setWidgetProperty('button4',-1, 'None')

		# elif (currQuestion == 11 or currQuestion == 12 or currQuestion == 13 ) and btnAction == 1: 
		# 	if currQuestion == 11:
		# 		self.setWidgetProperty('button1',1, 'Done')
		# 		self.setWidgetProperty('button2',-1, 'None')

		# 	saySomething(characterName, questions[currQuestion])
		# 	currQuestion+=1
		# elif currQuestion == 14 and btnAction == 1: 
		# 	saySomething(characterName, questions[currQuestion])
		# 	currQuestion+=1
		# elif currQuestion == 15 and btnAction == 1:
		# 	saySomething(characterName, 'Thank you!')
		# 	currQuestion+=1

		# self.currQuestion = currQuestion
		# self.isNotDoneHearing = isNotDoneHearing
	def eventButtonClick(self, btnName):
		print "button clicked"

	def eventDialogButton(self, dialogName, action, message):
		print 'pyscript dialogButtonEvent'
		if dialogName == 'exitBox':
			if action == 1:
				self.exitApp()
				
androidEngine = MyVHEngine()
setVHEngine(androidEngine)		
	
scene.addAssetPath("script", "scripts")
scene.addAssetPath("motion", "characters")
scene.setBoolAttribute("internalAudio", True)
scene.run("camera.py")
scene.run("light.py")
scene.run("setupCharacter.py")
setupCharacter(characterName, characterName, "", "")
character = scene.getCharacter(characterName)
bml.execBML(characterName, '<body posture="ChrBrad@Idle01"/>')
bml.execBML(characterName, '<saccade mode="listen"/>')
sim.start()

# start the first utterance
#scene.command('bml char ChrRachel file ./Sounds/intro_age_1.xml')
