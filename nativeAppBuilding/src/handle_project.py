'''
Catroid: An on-device graphical programming language for Android devices
Copyright (C) 2010-2012 The Catrobat Team
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
import codecs
import glob
import subprocess

'''
Automatically build and sign Catroid application.

python handle_project.py <path_to_project> <path_to_catroid> <project_id> <output_folder>

Example:
python handle_project.py test.zip ~/hg/catroid 42 .
'''

#Constants
PROJECTCODE_NAME = 'projectcode.xml'
ANT_BUILD_TARGET = 'debug'

def unzip_project(archive_name):
    project_name = os.path.splitext(archive_name)[0]
    zipfile.ZipFile(archive_name).extractall(project_name)

def verify_checksum(path_to_file):
    filename = os.path.basename(path_to_file)
    checksum = filename.split('_', 1)[0]
    file_contents = open(os.path.join(path_to_file), 'rb').read()
    if checksum == hashlib.md5(file_contents).hexdigest().upper():
        return True
    else:
        return False

def rename_file_in_project(old_name, new_name, project_file_path, resource_type):
    doc = xml.dom.minidom.parse(project_file_path)

    # TODO: Check if filename of sound files can include project name
    #OLD: if resource_type == 'images':
    #OLD:     tag_name = 'costumeFileName'
    #OLD: elif resource_type == 'sounds':
    #OLD:     tag_name = 'fileName'

    tag_name = 'fileName'

    for node in doc.getElementsByTagName(tag_name):
        if node.childNodes[0].nodeValue == old_name:
            node.childNodes[0].nodeValue = new_name
       
    f = codecs.open(project_file_path, 'wb', 'utf-8')
    doc.writexml(f, encoding='utf-8')
    f.close()

def rename_resources(path_to_project, project_name):
    res_token = 'resource'
    res_count = 0
    for resource_type in ['images', 'sounds']:
        path = os.path.join(path_to_project, resource_type)
        for filename in os.listdir(path):
            if filename == '.nomedia':
                continue
            basename, extension = os.path.splitext(filename)
            if verify_checksum(os.path.join(path, filename)):
                new_filename = res_token + str(res_count) + extension
                rename_file_in_project(filename, new_filename,\
                                    os.path.join(path_to_project, PROJECTCODE_NAME),\
                                    resource_type)
                os.rename(os.path.join(path, filename),\
                           os.path.join(path, new_filename))
                res_count = res_count + 1
            else:
                print 'Wrong checksum for file', filename
                exit(1)

def copy_project(path_to_catroid, path_to_project):
    shutil.copytree(path_to_catroid, os.path.join(path_to_project, 'catroid'))

    for resource_type in ['images', 'sounds']:
        if not os.path.exists(os.path.join(path_to_project, 'catroid', 'assets', resource_type)):
            os.makedirs(os.path.join(path_to_project, 'catroid', 'assets', resource_type))
        for filename in os.listdir(os.path.join(path_to_project, resource_type)):
            shutil.move(os.path.join(path_to_project, resource_type, filename),\
                    os.path.join(path_to_project, 'catroid', 'assets', resource_type, filename))

    shutil.move(os.path.join(path_to_project, PROJECTCODE_NAME),\
                    os.path.join(path_to_project, 'catroid', 'assets', PROJECTCODE_NAME))

def set_project_name(new_name, path_to_file):
    doc = xml.dom.minidom.parse(path_to_file)

    for node in doc.getElementsByTagName('string'):
        if node.attributes.item(0).value == 'app_name':
            node.childNodes[0].nodeValue = new_name
    
    f = codecs.open(path_to_file, 'wb', 'utf-8')
    doc.writexml(f, encoding='utf-8')
    f.close()
    
def get_project_name(project_filename):
    for node in xml.dom.minidom.parse(project_filename).getElementsByTagName('projectName'):
        if node.parentNode.nodeName == 'Content.Project': 
            return node.childNodes[0].nodeValue

def rename_package(path_to_project, new_package):
    catroid_package = 'at.tugraz.ist.catroid'
    path_to_source = os.path.join(path_to_project, 'catroid', 'src', 'at', 'tugraz', 'ist')
    os.rename(os.path.join(path_to_source, 'catroid'),\
              os.path.join(path_to_source, new_package))
    os.mkdir(os.path.join(path_to_source, 'catroid'))
    shutil.move(os.path.join(path_to_source, new_package),\
                os.path.join(path_to_source, 'catroid'))
    for root, dirs, files in os.walk(path_to_project):
        for name in files:
            if os.path.splitext(name)[1] in ('.java', '.xml'):
                for line in fileinput.input(os.path.join(root, name), inplace=1):
                    if catroid_package in line:
                        line = line.replace(catroid_package, catroid_package + '.' + new_package)
                    sys.stdout.write(line)

def edit_manifest(path_to_project, permissions):
    path_to_manifest = os.path.join(path_to_project, 'catroid', 'AndroidManifest.xml')
    doc = xml.dom.minidom.parse(path_to_manifest)

    for node in doc.getElementsByTagName('uses-permission'):
        if not node.attributes.item(0).value in permissions:
            node.parentNode.removeChild(node)

    for node in doc.getElementsByTagName('activity'):
        for i in range(0, node.attributes.length):
            if node.attributes.item(i).name == 'android:name':
                if node.attributes.item(i).value == '.ui.MainMenuActivity':
                   node.attributes.item(i).value = '.stage.NativeAppActivity'        

    f = codecs.open(path_to_manifest, 'wb', 'utf-8')
    doc.writexml(f, encoding='utf-8')
    f.close()
    
def getNecessaryPermissions(path_to_projectcode):
   permissions = []
   projectcode = codecs.open(path_to_projectcode)
   read = projectcode.read()
   if 'NXTMotor' in read:
       permissions.append('android.permission.BLUETOOTH')
   return permissions

def main():
    if len(sys.argv) != 6:
        print 'Invalid arguments. Correct usage:'
        print 'python handle_project.py <path_to_project> <path_to_catroid>\
                <path_to_libs> <project_id> <output_folder>'
        return 1

    path_to_project, archive_name = os.path.split(sys.argv[1])
    project_filename = os.path.splitext(archive_name)[0]

    path_to_catroid = sys.argv[2]
    path_to_lib = sys.argv[3]
    project_id = sys.argv[4]
    output_folder = sys.argv[5]

    if os.path.exists(os.path.join(path_to_project, project_filename)):
        shutil.rmtree(os.path.join(path_to_project, project_filename))

    unzip_project(os.path.join(path_to_project, archive_name))
    path_to_project = os.path.join(path_to_project, project_filename)
    


    permissions = getNecessaryPermissions(os.path.join(path_to_project, PROJECTCODE_NAME))

    rename_resources(path_to_project, project_filename)

    project_name = get_project_name(os.path.join(path_to_project, PROJECTCODE_NAME))
    copy_project(path_to_catroid, path_to_project)
    
    shutil.copytree(path_to_lib, os.path.join(path_to_project, 'libraryProjects'))

#Update build.xml files / Generate if not available.
    with open(os.devnull, 'wb') as devnull:
        subprocess.check_call(['android', 'update', 'project', '-p',
            os.path.join(path_to_project, 'catroid')],
            stdout=devnull)
        subprocess.check_call(['android', 'update', 'lib-project', '-p',
            os.path.join(path_to_project, 'libraryProjects/actionbarsherlock')],
            stdout=devnull)

    if os.path.exists(os.path.join(path_to_project, 'catroid', 'gen')):
        shutil.rmtree(os.path.join(path_to_project, 'catroid', 'gen'))

    edit_manifest(path_to_project, permissions)

    rename_package(path_to_project, 'app_' + str(project_id))

    language_dirs = glob.glob(os.path.join(path_to_project, 'catroid', 'res', 'values*'))
    for curr_lang_dir in language_dirs:
        editing_file = os.path.join(curr_lang_dir, 'strings.xml')
        if os.path.exists(editing_file):
            set_project_name(project_name, editing_file)

    with open(os.devnull, 'wb') as devnull:
        subprocess.check_call(['ant', ANT_BUILD_TARGET ,'-f',
            os.path.join(path_to_project, 'catroid', 'build.xml')],
            stdout=devnull)

    for filename in os.listdir(os.path.join(path_to_project, 'catroid', 'bin')):
        if filename.endswith('.apk'):
            shutil.move(os.path.join(path_to_project, 'catroid', 'bin', filename),\
                        os.path.join(output_folder, project_filename + '.apk'))

    shutil.rmtree(path_to_project)
    return 0

if __name__ == '__main__':
    main()
