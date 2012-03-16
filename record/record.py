'''
Catroid: An on-device graphical programming language for Android devices
Copyright (C) 2010-2011 The Catroid Team
(<http://code.google.com/p/catroid/wiki/Credits>)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
'''

import os
import sys
import zipfile
import shutil
import fileinput
import hashlib
import xml.dom.minidom
import urllib2
import numpy as np, cv
from PIL import Image

'''
Creates video from the project archive and uploads it to youtube.

python record.py <path_to_project_archive>

Example:
python record.py test.zip
'''

class Volume:
    def __init__(self, volume, timestamp):
        self.volume = float(volume)
        self.timestamp = int(timestamp)

class SoundInfo:
    def __init__(self, filename, isPlaying, timestamp):
        self.filename = filename
        self.isPlaying = isPlaying == 'true'
        self.timestamp = int(timestamp)

class Costume:
    def __init__(self, name, rotation, scaleX, scaleY, visible, x, y, z, filename, show, brightness, alpha, timestamp, path_to_project):
        self.name = name
        self.rotation = float(rotation)
        self.scaleX = float(scaleX)
        self.scaleY = float(scaleY)
        self.visible = bool(visible)
        self.x = int(float(x))
        self.y = int(float(y))
        self.z = int(float(z))
        self.filename = filename
        self.image = Image.open(os.path.join(path_to_project, 'images', self.filename))
        self.show = bool(show)
        self.brightness = float(brightness)
        self.alpha = float(alpha)
        self.timestamp = int(timestamp)

def unzip_project(archive_name):
    project_name = os.path.splitext(archive_name)[0]
    zipfile.ZipFile(archive_name).extractall(project_name)

def parse_xml(path_to_project):
    doc = xml.dom.minidom.parse(os.path.join(path_to_project, 'record.xml'))
    stageEvents = []
    for node in doc.getElementsByTagName('Pair'):
        for child in node.childNodes:
            if child.nodeName == 'first':
                if child.attributes.item(0).value == 'Volume':
                    stageEvents.append(getVolume(node))
                elif child.attributes.item(0).value == 'Costume':
                    stageEvents.append(getCostume(node, path_to_project))
                elif child.attributes.item(0).value == 'SoundInfo':
                    stageEvents.append(getSoundInfo(node))
    return stageEvents

def getVolume(node):
    return Volume(node.getElementsByTagName('volume')[0].firstChild.nodeValue,\
                  node.getElementsByTagName('second')[0].firstChild.nodeValue)

def getCostume(node, path_to_project):
    return Costume(node.getElementsByTagName('name')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('rotation')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('scaleX')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('scaleY')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('visible')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('x')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('y')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('zPosition')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('fileName')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('show')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('brightnessValue')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('alphaValue')[0].firstChild.nodeValue,\
                   node.getElementsByTagName('second')[0].firstChild.nodeValue,\
                   path_to_project)

def getSoundInfo(node):
    return SoundInfo(node.getElementsByTagName('fileName')[0].firstChild.nodeValue,\
                     node.getElementsByTagName('isPlaying')[0].firstChild.nodeValue,\
                     node.getElementsByTagName('second')[0].firstChild.nodeValue)
    

def write_video(stageEvents, path_to_project):
    screen_elements = []
    screen_frame = update_screen(screen_elements)
    current_timestamp = 0
    writer = cv.CreateVideoWriter(os.path.join(path_to_project, 'out.avi'), cv.CV_FOURCC('X','V','I','D'), 30, (480,800), 1)
    for event in stageEvents:
        while current_timestamp < event.timestamp:
            cv.WriteFrame(writer, screen_frame)
            current_timestamp += 33.33333
        if isinstance(event, Costume):
            if screen_elements == []:
                screen_elements.append(event)
                screen_frame = update_screen(screen_elements)
            else:
                for element in screen_elements:
                    if element.name == event.name:
                        screen_elements[screen_elements.index(element)] = event
                        screen_frame = update_screen(screen_elements)
                        break
                else:
                    for i in range(len(screen_elements)):
                        if screen_elements[i].z > event.z:
                            screen_elements.insert(i, event)
                            break
                        elif i == len(screen_elements) - 1:
                            screen_elements.append(event)
                            screen_frame = update_screen(screen_elements)
                            break
                    else:
                        screen_elements.append(event)
                        screen_frame = update_screen(screen_elements)
    del writer
    return current_timestamp


def update_screen(screen_elements):
    blank_image = Image.new("RGB", (480, 800))
    for element in screen_elements:
        img = element.image.resize((int(element.image.size[0]*element.scaleX), int(element.image.size[1]*element.scaleY)))
        img = img.rotate(element.rotation, expand = True)
        if len(img.getbands()) > 3:
            b, a = element.brightness, element.alpha
            img.putdata(map(lambda x: (int(x[0]*b), int(x[1]*b), int(x[2]*b), int(x[3]*a)), list(img.getdata())))
            blank_image.paste(img, (element.x+240, element.y+400), img)
        else:
            if element.brightness != 1 or element.alpha != 1:
                img = img.convert('RGB')
                img = img.point(lambda x: x*element.brightness)
                img.putalpha(int(element.alpha*255))
            blank_image.paste(img, (element.x+240, element.y+400))
    
    cv_img = cv.CreateImageHeader(blank_image.size, cv.IPL_DEPTH_8U, 3)
    cv.SetData(cv_img, blank_image.tostring(), blank_image.size[0]*3)
    cv.CvtColor(cv_img, cv_img, cv.CV_BGR2RGB)
    return cv_img


def add_sound(stageEvents, video_length, path_to_project):
    sounds = []
    for i in range(len(stageEvents)):
        if isinstance(stageEvents[i], SoundInfo):
            if stageEvents[i].isPlaying:
                if i < len(stageEvents) - 1:
                    for j in range(i+1, len(stageEvents)):
                        if (isinstance(stageEvents[j], SoundInfo)) and (stageEvents[j].filename == stageEvents[i].filename) and (not stageEvents[j].isPlaying):
                            sounds.append((stageEvents[i].filename, stageEvents[i].timestamp, stageEvents[j].timestamp))
                            break
                    else:
                        sounds.append((stageEvents[i].filename, stageEvents[i].timestamp, video_length))
                else:
                    sounds.append((stageEvents[i].filename, stageEvents[i].timestamp, video_length))

    sound_mix_command = 'sox --combine mix-power '

    for s in sounds:
        sound_mix_command += ' "|sox \'{0}\' -r 44100 -p trim 0 {1} pad {2} 0" '\
                            .format( os.path.join(path_to_project, 'sounds', s[0]), str((s[2] - s[1])/1000.0), str(s[1]/1000.0) )
    
    sound_mix_command += '\'' + os.path.join(path_to_project, 'soundtrack.mp3') + '\''
    os.system(sound_mix_command)
    os.system('ffmpeg -i "{0}" -i "{1}" "{2}"'.format(os.path.join(path_to_project, 'soundtrack.mp3'), os.path.join(path_to_project, 'out.avi'), os.path.join(path_to_project, 'result.avi')))

def upload(path_to_project):
    DEV_KEY = 'AI39si5FEjazuKSkgPMH_1cppVPzUiNLl19UnfzkhvjrFwwbQc4wueHT7CR1oWA__WA5L27INddl9m6UigdcFZaTmvp7h8yUPQ'
    USERNAME = 'test.lexmiir@gmail.com'
    PASSWORD = 'testpassword'
    APP_NAME = 'test.lexmiir'
    boundary_string = 'boundarystring'
    api_xml_request = '''<?xml version="1.0"?>
<entry xmlns="http://www.w3.org/2005/Atom"
  xmlns:media="http://search.yahoo.com/mrss/"
  xmlns:yt="http://gdata.youtube.com/schemas/2007">
  <media:group>
    <media:title type="plain">Test title</media:title>
    <media:description type="plain">
      This is a test of automated video uploading
    </media:description>
    <media:category
      scheme="http://gdata.youtube.com/schemas/2007/categories.cat">Entertainment
    </media:category>
    <media:keywords>test</media:keywords>
  </media:group>
</entry>'''

    post_content = '''--{0}
Content-Type: application/atom+xml; charset=UTF-8

{1}
--{0}
Content-Type: {2}
Content-Transfer-Encoding: binary

{3}
--{0}--'''.format(boundary_string, api_xml_request, 'video/x-msvideo', open(os.path.join(path_to_project, 'result.avi'), 'rb').read())

    req = urllib2.Request('https://www.google.com/accounts/ClientLogin')
    req.add_header('Content-Type', 'application/x-www-form-urlencoded')
    req.add_data('Email={0}&Passwd={1}&service=youtube&source={2}'.format(USERNAME, PASSWORD, APP_NAME))
    r = urllib2.urlopen(req)
    data = r.read()
    auth = data.rsplit('Auth=',1)[1][0:-1]

    req = urllib2.Request('http://uploads.gdata.youtube.com/feeds/api/users/testlexmiir/uploads')
    req.add_header('Authorization', 'GoogleLogin Auth={0}'.format(auth))
    req.add_header('GData-Version', '2')
    req.add_header('X-GData-Key', 'key=' + DEV_KEY)
    req.add_header('Slug', 'result.avi')
    req.add_header('Content-Type', 'multipart/related; boundary="{0}"'.format(boundary_string))
    req.add_data(post_content)

    r = urllib2.urlopen(req)
    data = r.read()
    print data

def main():
    if len(sys.argv) != 2:
        print 'Invalid arguments. Correct usage:'
        print 'python record.py <path_to_project_archive>'
        return 1
    path_to_project, archive_name = os.path.split(sys.argv[1])
    project_filename = os.path.splitext(archive_name)[0]
    if os.path.exists(os.path.join(path_to_project, project_filename)):
        shutil.rmtree(os.path.join(path_to_project, project_filename))
    unzip_project(os.path.join(path_to_project, archive_name))
    path_to_project = os.path.join(path_to_project, project_filename)
    stageEvents = parse_xml(path_to_project)
    stageEvents.sort(key = lambda x: x.timestamp)
    video_length = write_video(stageEvents, path_to_project)
    add_sound(stageEvents, video_length, path_to_project)
    upload(path_to_project)


if __name__ == '__main__':
    main()
