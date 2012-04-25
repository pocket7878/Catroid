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
import re
import shutil
import fileinput
import hashlib
import xml.dom.minidom
import urllib2
sys.path.append('/usr/lib/pymodules/python2.6')
sys.path.append('/usr/lib/python2.5/site-packages/')
import cv
from PIL import Image

'''
Creates video from the project archive and uploads it to youtube.

python record.py <path_to_project_archive>

Example:
python record.py test.zip
'''

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
        self.visible = visible == 'true'
        self.x = int(float(x))
        self.y = int(float(y))
        self.z = int(float(z))
        self.filename = filename
        self.show = show == 'true'
        self.brightness = float(brightness)
        self.alpha = float(alpha)
        self.timestamp = int(timestamp)

    def init(self):
        self.load_image()
        self.update_image()
        self.update_draw_x()
        self.update_draw_y()

    def update_draw_x(self):
        self.drawX = int(self.x+Consts.screenWidth/2 + (self.image.size[0] - self.image.size[0]*self.scaleX)/2)

    def update_draw_y(self):
        self.drawY = int(-self.y+Consts.screenHeight/2 - self.image.size[1] + (self.image.size[1] - self.image.size[1]*self.scaleY)/2)

    def load_image(self):
        self.image = Image.open(os.path.join(Consts.path_to_project, 'images', self.filename))
        if len(self.image.getbands()) <= 3:
            self.image = self.image.convert('RGBA')
        self.drawImage = self.image

    def update_image(self):
        brightness, alpha = self.brightness, self.alpha
        self.drawImage = self.image.resize((int(self.image.size[0]*self.scaleX), int(self.image.size[1]*self.scaleY))).rotate(self.rotation, expand = True)
        self.drawImage.putdata(map(lambda x: (int(x[0]*brightness), int(x[1]*brightness), int(x[2]*brightness), int(x[3]*alpha)), list(self.drawImage.getdata())))


    def update(self, new_costume):
        self.timestamp = new_costume.timestamp

        if new_costume.filename != self.filename:
            self.filename = new_costume.filename
            self.load_image()
            self.update_image()

        if new_costume.brightness != self.brightness or new_costume.alpha != self.alpha or new_costume.scaleX != self.scaleX or new_costume.scaleY != self.scaleY or new_costume.rotation != self.rotation:
            self.brightness = new_costume.brightness
            self.alpha = new_costume.alpha
            self.scaleX = new_costume.scaleX
            self.scaleY = new_costume.scaleY
            self.rotation = new_costume.rotation
            self.update_image()

        if new_costume.x != self.x:
            self.x = new_costume.x
            self.update_draw_x()

        if new_costume.y != self.y:
            self.y = new_costume.y
            self.update_draw_y()

        if new_costume.z != self.z:
            self.z = new_costume.z

        if new_costume.show != self.show:
            self.show = new_costume.show

        if new_costume.visible != self.visible:
            self.visible = new_costume.visible

class Consts:
    pass

def unzip_project(archive_name):
    project_name = os.path.splitext(archive_name)[0]
    zipfile.ZipFile(archive_name).extractall(project_name)

def parse_xml(path_to_project):
    doc = xml.dom.minidom.parse(os.path.join(path_to_project, 'record.xml'))
    costumeEvents = []
    soundsEvents = []

    for node in doc.getElementsByTagName('Recording'):
        for child in node.childNodes:
            if child.nodeName == 'duration':
                duration = int(child.firstChild.nodeValue)
            elif child.nodeName == 'screenHeight':
                screenHeight = int(child.firstChild.nodeValue)
                Consts.screenHeight = screenHeight
            elif child.nodeName == 'screenWidth':
                screenWidth = int(child.firstChild.nodeValue)
                Consts.screenWidth = screenWidth

    for node in doc.getElementsByTagName('costumeList'):
        for child in node.childNodes:
            if child.nodeName == 'RecordedCostume':
                try:
                    costumeEvents.append(getCostume(child, path_to_project))
                except IndexError:
                    pass #Costume without a filename


    for node in doc.getElementsByTagName('soundList'):
        for child in node.childNodes:
            if child.nodeName == 'RecordedSound':
                soundsEvents.append(getSoundInfo(child))


    return costumeEvents, soundsEvents, duration, screenWidth, screenHeight


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
                   node.getElementsByTagName('timestamp')[0].firstChild.nodeValue,\
                   path_to_project)

def getSoundInfo(node):
    return SoundInfo(node.getElementsByTagName('fileName')[0].firstChild.nodeValue,\
                     node.getElementsByTagName('isPlaying')[0].firstChild.nodeValue,\
                     node.getElementsByTagName('timestamp')[0].firstChild.nodeValue)
    

def write_video(costumeEvents, duration, screenWidth, screenHeight, path_to_project):
    screen_elements = []
    screen_frame = update_screen(screen_elements, screenWidth, screenHeight)
    current_timestamp = 0
    writer = cv.CreateVideoWriter(os.path.join(path_to_project, 'video.avi'), cv.CV_FOURCC('X','V','I','D'), 30\
                                ,(screenWidth,screenHeight), 1)
    
    for event in costumeEvents:
        while current_timestamp < event.timestamp:
            cv.WriteFrame(writer, screen_frame)
            current_timestamp += 33.33333
        if isinstance(event, Costume):
            if screen_elements == []:
                event.init()
                screen_elements.append(event)
                screen_frame = update_screen(screen_elements, screenWidth, screenHeight)
            else:
                for element in screen_elements:
                    if element.name == event.name:
                        screen_elements[screen_elements.index(element)].update(event)
                        screen_frame = update_screen(screen_elements, screenWidth, screenHeight)
                        break
                else:
                    event.init()
                    screen_elements.append(event)
                    screen_frame = update_screen(screen_elements, screenWidth, screenHeight)
    while current_timestamp < duration:
            cv.WriteFrame(writer, screen_frame)
            current_timestamp += 33.33333
    del writer


def update_screen(screen_elements, screenWidth, screenHeight):
    blank_image = Image.new("RGB", (screenWidth, screenHeight))
    screen_elements.sort(key = lambda element: element.z)
    for element in screen_elements:
        if element.visible and element.show:
            blank_image.paste(element.drawImage, (element.drawX, element.drawY), element.drawImage)
    cv_img = cv.CreateImageHeader(blank_image.size, cv.IPL_DEPTH_8U, 3)
    cv.SetData(cv_img, blank_image.tostring(), blank_image.size[0]*3)
    cv.CvtColor(cv_img, cv_img, cv.CV_BGR2RGB)
    return cv_img


def add_sound(soundEvents, duration, path_to_project):
    sounds = []
    for i in range(len(soundEvents)):
        if isinstance(soundEvents[i], SoundInfo):
            if soundEvents[i].isPlaying:
                if i < len(soundEvents) - 1:
                    for j in range(i+1, len(soundEvents)):
                        if (isinstance(soundEvents[j], SoundInfo)) and (soundEvents[j].filename == soundEvents[i].filename) and (not soundEvents[j].isPlaying):
                            sounds.append((soundEvents[i].filename, soundEvents[i].timestamp, soundEvents[j].timestamp))
                            break
                    else:
                        sounds.append((soundEvents[i].filename, soundEvents[i].timestamp, duration))
                else:
                    sounds.append((soundEvents[i].filename, soundEvents[i].timestamp, duration))

    if len(sounds) > 0:
        if len(sounds) >= 2:
            sound_mix_command = 'sox -V1 --combine mix-power '

            for s in sounds:
                sound_mix_command += ' "|sox -V1 \'{0}\' -r 44100 -p trim 0 {1} pad {2} 0" '\
                                    .format( os.path.join(path_to_project, 'sounds', s[0]), str((s[2] - s[1])/1000.0), str(s[1]/1000.0) )
            sound_mix_command += '\'' + os.path.join(path_to_project, 'soundtrack.mp3') + '\''

        elif len(sounds) == 1:
            sound_mix_command = 'sox -V1 \'{0}\' -r 44100 {3} trim 0 {1} pad {2} 0 '\
                                    .format( os.path.join(path_to_project, 'sounds', sounds[0][0]), str((sounds[0][2] - sounds[0][1])/1000.0), str(sounds[0][1]/1000.0), '\'' + os.path.join(path_to_project, 'soundtrack.mp3') + '\'' )

        
        os.system(sound_mix_command)
        if os.path.exists(os.path.join(path_to_project, 'soundtrack.mp3')):
            os.system('ffmpeg -i "{0}" -i "{1}" "{2}"'.format(os.path.join(path_to_project, 'soundtrack.mp3'), os.path.join(path_to_project, 'video.avi'), os.path.join(path_to_project, 'out.avi')))
        else:
            os.rename(os.path.join(path_to_project, 'video.avi'), os.path.join(path_to_project, 'out.avi'))

def upload(path_to_project, title, description):
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
    <media:title type="plain">{0}</media:title>
    <media:description type="plain">
      {1}
    </media:description>
    <media:category
      scheme="http://gdata.youtube.com/schemas/2007/categories.cat">Entertainment
    </media:category>
    <media:keywords>test</media:keywords>
  </media:group>
</entry>'''.format(title, description)

    post_content = '''--{0}
Content-Type: application/atom+xml; charset=UTF-8

{1}
--{0}
Content-Type: {2}
Content-Transfer-Encoding: binary

{3}
--{0}--'''.format(boundary_string, api_xml_request, 'video/x-msvideo', open(os.path.join(path_to_project, 'out.avi'), 'rb').read())

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
    req.add_header('Slug', 'out.avi')
    req.add_header('Content-Type', 'multipart/related; boundary="{0}"'.format(boundary_string))
    req.add_data(post_content)

    r = urllib2.urlopen(req)
    data = r.read()
    print ''
    for each in re.findall(r"<media:player url='(.+?)'/>", data):
        print each

def main():
    if len(sys.argv) != 4:
        print 'Invalid arguments. Correct usage:'
        print 'python record.py <path_to_project_archive> <title> <description>'
        return 1
    path_to_project, archive_name = os.path.split(sys.argv[1])
    title = sys.argv[2]
    description = sys.argv[3]
    project_filename = os.path.splitext(archive_name)[0]
    if os.path.exists(os.path.join(path_to_project, project_filename)):
        shutil.rmtree(os.path.join(path_to_project, project_filename))
    unzip_project(os.path.join(path_to_project, archive_name))
    path_to_project = os.path.join(path_to_project, project_filename)
    Consts.path_to_project = path_to_project
    costumeEvents, soundEvents, duration, screenWidth, screenHeight = parse_xml(path_to_project)
    costumeEvents.sort(key = lambda x: x.timestamp)
    soundEvents.sort(key = lambda x: x.timestamp)
    write_video(costumeEvents, duration, screenWidth, screenHeight, path_to_project)
    add_sound(soundEvents, duration, path_to_project)
    upload(path_to_project, title, description)


if __name__ == '__main__':
    main()
