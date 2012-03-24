import unittest
import sys
import os
import shutil
import filecmp

sys.path.append(sys.path[0])

from src.record import Costume
from src.record import SoundInfo
from src.record import parse_xml

class TesthandleProject(unittest.TestCase):

    def setUp(self):
        self.path_to_project = 'testProject'
        self.costumeName = 'costumeName'
        self.costumeFileName = 'costumeFileName'
        self.rotation = 0.0
        self.scaleX = 1.0
        self.scaleY = 1.0
        self.visible = True;
        self.show = True;
        self.x = 200.0
        self.y = 300.0
        self.brightnessValue = 1.0
        self.alphaValue = 1.0
        self.zPosition = 0
        self.costumeTimestamp = 100

        self.soundName = 'soundName'
        self.soundFileName = 'soundFileName.mp3'
        self.isPlaying = True
        self.soundTimestamp = 200

        self.duration = 500
        self.screenWidth = 480
        self.screenHeight = 800


    def test_parse_xml(self):
        costumeEvents, soundEvents, duration, screenWidth, screenHeight = parse_xml(self.path_to_project)
        self.assertEquals(len(costumeEvents), 1)
        costume = costumeEvents[0]
        self.assertEquals(costume.name, self.costumeName)
        self.assertEquals(costume.filename, self.costumeFileName)
        self.assertEquals(costume.rotation, self.rotation)
        self.assertEquals(costume.scaleX, self.scaleX)
        self.assertEquals(costume.scaleY, self.scaleY)
        self.assertEquals(costume.x, self.x)
        self.assertEquals(costume.y, self.y)
        self.assertEquals(costume.z, self.zPosition)
        self.assertEquals(costume.visible, self.visible)
        self.assertEquals(costume.show, self.show)
        self.assertEquals(costume.brightness, self.brightnessValue)
        self.assertEquals(costume.alpha, self.alphaValue)
        self.assertEquals(costume.timestamp, self.costumeTimestamp)

        self.assertEquals(len(soundEvents), 1)
        sound = soundEvents[0]
        self.assertEquals(sound.filename, self.soundFileName)
        self.assertEquals(sound.isPlaying, self.isPlaying)
        self.assertEquals(sound.timestamp, self.soundTimestamp)
        
        self.assertEquals(duration, self.duration)
        self.assertEquals(screenWidth, self.screenWidth)
        self.assertEquals(screenHeight, self.screenHeight)


if __name__ == '__main__':
    unittest.main()
