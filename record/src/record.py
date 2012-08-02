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
import tempfile
import json
import urllib2
sys.path.append('/usr/lib/pymodules/python2.6')
sys.path.append('/usr/lib/python2.5/site-packages/')
import cv
from PIL import Image

'''
Creates video from the project archive and uploads it to youtube.

python record.py <path_to_project_archive> <title> <description>

Example:
python record.py test.zip 'Video Title' 'Video Description'
'''

class SoundInfo:
    def __init__(self, filename, isPlaying, timestamp):
        self.filename = Values.changed_filenames[filename]
        self.isPlaying = isPlaying
        self.timestamp = int(timestamp)

class Costume:
    def __init__(self, name, rotation, scaleX, scaleY, visible, x, y, z, filename, show, brightness, alpha, timestamp, path_to_project):
        self.name = name
        self.rotation = rotation
        self.scaleX = scaleX
        self.scaleY = scaleY
        self.visible = visible
        self.x = int(x)
        self.y = int(y)
        self.z = int(z)
        self.filename = Values.changed_filenames[filename]
        self.show = show
        self.brightness = brightness
        self.alpha = alpha
        self.timestamp = int(timestamp)

    def init(self):
        self.load_image()
        self.update_image()
        self.update_draw_x()
        self.update_draw_y()

    def update_draw_x(self):
        self.drawX = int(self.x+Values.screenWidth/2 + (self.image.size[0] - self.drawImage.size[0])/2)

    def update_draw_y(self):
        self.drawY = int(-self.y+Values.screenHeight/2 - self.image.size[1] + (self.image.size[1] - self.drawImage.size[1])/2)

    def load_image(self):
        self.image = Image.open(os.path.join(Values.path_to_project, 'images', self.filename))
        if len(self.image.getbands()) <= 3:
            self.image = self.image.convert('RGBA')
        self.drawImage = self.image

    def update_image(self):
        brightness, alpha = self.brightness, self.alpha
        self.drawImage = self.image.resize((int(self.image.size[0]*self.scaleX), int(self.image.size[1]*self.scaleY))).rotate(self.rotation, expand = True)
        self.drawImage.putdata(map(lambda x: (int(x[0]*brightness), int(x[1]*brightness), int(x[2]*brightness), int(x[3]*alpha)), list(self.drawImage.getdata())))


    def update(self, new_costume):
        self.timestamp = new_costume.timestamp

        needs_update = False

        if new_costume.filename != self.filename:
            self.filename = new_costume.filename
            self.load_image()
            needs_update = True

        if new_costume.brightness != self.brightness:
            self.brightness = new_costume.brightness
            needs_update = True

        if new_costume.alpha != self.alpha:
            self.alpha = new_costume.alpha
            needs_update = True

        if new_costume.scaleX != self.scaleX:
            self.scaleX = new_costume.scaleX
            needs_update = True

        if new_costume.scaleY != self.scaleY:
            self.scaleY = new_costume.scaleY
            needs_update = True

        if new_costume.rotation != self.rotation:
            self.rotation = new_costume.rotation
            needs_update = True

        if new_costume.x != self.x:
            self.x = new_costume.x
            needs_update = True

        if new_costume.y != self.y:
            self.y = new_costume.y
            needs_update = True

        if needs_update:
            self.update_image()
            self.update_draw_x()
            self.update_draw_y()

        if new_costume.z != self.z:
            self.z = new_costume.z

        if new_costume.show != self.show:
            self.show = new_costume.show

        if new_costume.visible != self.visible:
            self.visible = new_costume.visible

class Values:
    changed_filenames = {}

def unzip_project(path_to_archive):
    Values.path_to_project = tempfile.mkdtemp()
    archive = zipfile.ZipFile(path_to_archive)
    images_count, sounds_count = 0, 0
    for filename in archive.namelist():
        if filename.endswith('/'):
            os.makedirs(os.path.join(Values.path_to_project, filename))
        elif not filename.endswith('.nomedia'):
            if filename.startswith('images/'):
                new_filename = 'image' + str(images_count)
                images_count += 1
                shutil.copyfileobj(archive.open(filename), open(os.path.join(Values.path_to_project, 'images', new_filename), 'w'))
                Values.changed_filenames[filename.decode('utf-8').split('/')[1]] = new_filename
            elif filename.startswith('sounds/'):
                new_filename = 'sound' + str(sounds_count) + '.' + filename.decode('utf-8').split('/')[1].rsplit('.')[1]
                sounds_count += 1
                shutil.copyfileobj(archive.open(filename), open(os.path.join(Values.path_to_project, 'sounds', new_filename), 'w'))
                Values.changed_filenames[filename.decode('utf-8').split('/')[1]] = new_filename
            else:
                shutil.copyfileobj(archive.open(filename), open(os.path.join(Values.path_to_project, filename), 'w'))

def parse_json():
    recording = json.load(open(os.path.join(Values.path_to_project, 'record.json')))

    Values.screenHeight = int(recording['screenHeight'])
    Values.screenWidth = int(recording['screenWidth'])
    Values.duration = int(recording['duration'])

    costumeEvents = []
    soundsEvents = []

    for costume in recording['costumes']:
        costumeEvent = getCostume(costume)
        if costumeEvent:
            costumeEvents.append(costumeEvent)


    for sound in recording['sounds']:
        soundsEvents.append(getSoundInfo(sound))


    return costumeEvents, soundsEvents


def getCostume(costume):
    if 'filename' in costume.keys():
        return Costume(costume['name'], costume['rotation'], costume['scaleX'], costume['scaleY'], costume['visible'],\
            costume['x'], costume['y'], costume['zPosition'], costume['filename'], costume['show'], costume['brightnessValue'],\
            costume['alphaValue'], costume['timestamp'], Values.path_to_project)
    else:
        return None

def getSoundInfo(sound):
    return SoundInfo(sound['filename'], sound['isPlaying'], sound['timestamp'])
    

def write_video(costumeEvents):
    screen_elements = []
    screen_frame = update_screen(screen_elements, Values.screenWidth, Values.screenHeight)
    current_timestamp = 0
    writer = cv.CreateVideoWriter(os.path.join(Values.path_to_project, 'video.avi'), cv.CV_FOURCC('X','V','I','D'), 30\
                                ,(Values.screenWidth, Values.screenHeight), 1)
    
    for event in costumeEvents:
        while current_timestamp < event.timestamp:
            cv.WriteFrame(writer, screen_frame)
            current_timestamp += 33.33333
        if isinstance(event, Costume):
            if screen_elements == []:
                event.init()
                screen_elements.append(event)
                screen_frame = update_screen(screen_elements, Values.screenWidth, Values.screenHeight)
            else:
                for element in screen_elements:
                    if element.name == event.name:
                        screen_elements[screen_elements.index(element)].update(event)
                        screen_frame = update_screen(screen_elements, Values.screenWidth, Values.screenHeight)
                        break
                else:
                    event.init()
                    screen_elements.append(event)
                    screen_frame = update_screen(screen_elements, Values.screenWidth, Values.screenHeight)
    while current_timestamp < Values.duration:
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


def add_sound(soundEvents):
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
                        sounds.append((soundEvents[i].filename, soundEvents[i].timestamp, Values.duration))
                else:
                    sounds.append((soundEvents[i].filename, soundEvents[i].timestamp, Values.duration))

    if len(sounds) > 0:
        if len(sounds) >= 2:
            sound_mix_command = 'sox -V1 --combine mix-power '

            for s in sounds:
                sound_mix_command += ' "|sox -V1 \'{0}\' -r 44100 -p trim 0 {1} pad {2} 0" '\
                                    .format( os.path.join(Values.path_to_project, 'sounds', s[0]), str((s[2] - s[1])/1000.0), str(s[1]/1000.0) )
            sound_mix_command += '\'' + os.path.join(Values.path_to_project, 'soundtrack.mp3') + '\''

        elif len(sounds) == 1:
            sound_mix_command = 'sox -V1 \'{0}\' -r 44100 {3} trim 0 {1} pad {2} 0 '\
                                    .format( os.path.join(Values.path_to_project, 'sounds', sounds[0][0]), str((sounds[0][2] - sounds[0][1])/1000.0), str(sounds[0][1]/1000.0), '\'' + os.path.join(Values.path_to_project, 'soundtrack.mp3') + '\'' )

        os.system(sound_mix_command)
    if os.path.exists(os.path.join(Values.path_to_project, 'soundtrack.mp3')):
        os.system('ffmpeg -i "{0}" -i "{1}" "{2}"'.format(os.path.join(Values.path_to_project, 'soundtrack.mp3'), os.path.join(Values.path_to_project, 'video.avi'), os.path.join(Values.path_to_project, 'out.avi')))
    else:
        os.rename(os.path.join(Values.path_to_project, 'video.avi'), os.path.join(Values.path_to_project, 'out.avi'))

def upload(title, description):
    DEV_KEY = 'AI39si5FEjazuKSkgPMH_1cppVPzUiNLl19UnfzkhvjrFwwbQc4wueHT7CR1oWA__WA5L27INddl9m6UigdcFZaTmvp7h8yUPQ'
    USERNAME = 'test.lexmiir@gmail.com'
    PASSWORD = 'testpassword'
    APP_NAME = 'test.lexmiir'
    boundary_string = 'boundarystring'
    api_xml_request = u'''<?xml version="1.0"?>
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
--{0}--'''.format(boundary_string, api_xml_request.encode('utf-8'), 'video/x-msvideo', open(os.path.join(Values.path_to_project, 'out.avi'), 'rb').read())

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
    path_to_project, archive_name = os.path.split(sys.argv[1].decode('utf-8'))
    title = sys.argv[2].decode('utf-8')
    description = sys.argv[3].decode('utf-8')
    project_filename = os.path.splitext(archive_name)[0]
    unzip_project(os.path.join(path_to_project, archive_name))
    costumeEvents, soundEvents = parse_json()
    costumeEvents.sort(key = lambda x: x.timestamp)
    soundEvents.sort(key = lambda x: x.timestamp)
    write_video(costumeEvents)
    add_sound(soundEvents)
    upload(title, description)


if __name__ == '__main__':
    main()
