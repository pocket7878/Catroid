'''
Catrobat: An on-device graphical programming language for Android devices
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
import argparse
import zipfile
import shutil
import fileinput
import hashlib
import xml.dom.minidom
import codecs
import glob
import subprocess
from tempfile import mkdtemp

#Constants
PROJECTCODE_NAME = 'projectcode.xml'
ANT_BUILD_TARGET = 'debug'

class ConversionMode:
    LIVE_WALLPAPER = 1
    NATIVE_APP = 2

class ConversionConfig:
    """Class to represent the configuration options for this script"""
    def __init__(self, args):
        self._working_dir = mkdtemp()
        self._path_to_project_archive, self._archive_name = os.path.split(args.project)
        self._verbose = args.verbose
        self._path_to_catroid = args.catrobatsrc
        self._path_to_lib = args.lib_src
        self._project_id = args.project_id
        self._output_dir = args.output_dir
        if (args.native_app == True):
            self._conversion_mode = ConversionMode.NATIVE_APP
        elif (args.live_wallpaper == True):
            self._conversion_mode = ConversionMode.LIVE_WALLPAPER
        else:
            print "Error, invalid mode in config, this mustn't happen"
            sys.exit()
    def getConversionMode(self):
        return self._conversion_mode
    def setPermissions(self, permissions):
        self._permissions = permissions
    def getPermissions(self):
        return self._permissions
    def getLibPath(self):
        return self._path_to_lib
    def getPathToCatroidSrc(self):
        return self._path_to_catroid
    def getProjectFilename(self):
        return os.path.splitext(self._archive_name)[0]
    def getProjectId(self):
        return self._project_id
    def isVerbose(self):
        return self._verbose
    def getWorkingDir(self):
        return self._working_dir
    def getPathToProject(self):
        return self._path_to_project
    def getFullProjectPath(self):
        return os.path.join(self._path_to_project_archive, self._archive_name)
    def getArchiveName(self):
        return self._archive_name
    def getPathToOutputDirectory(self):
        return self._output_dir
    def getPathToProjectCode(self):
        return os.path.join(self.getWorkingDir(), PROJECTCODE_NAME)


def unzip_project(archive_name, working_dir):
    project_name = os.path.splitext(archive_name)[0]
    zipfile.ZipFile(archive_name).extractall(working_dir)

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

def editManifest(config):
    path_to_manifest = os.path.join(config.getWorkingDir(), 'catroid', 'AndroidManifest.xml')
    doc = xml.dom.minidom.parse(path_to_manifest)

    for node in doc.getElementsByTagName('uses-permission'):
        if not node.attributes.item(0).value in config.getPermissions(): 
            node.parentNode.removeChild(node)

    for node in doc.getElementsByTagName('activity'):
        for i in range(0, node.attributes.length):
            if node.attributes.item(i).name == 'android:name':
                if node.attributes.item(i).value == '.ui.MainMenuActivity':
                   node.attributes.item(i).value = '.stage.NativeAppActivity'        

    f = codecs.open(path_to_manifest, 'wb', 'utf-8')
    doc.writexml(f, encoding='utf-8')
    f.close()
    
def acquireNecessaryPermissions(config):
   permissions = []
   projectcode = codecs.open(config.getPathToProjectCode())
   read = projectcode.read()
   if 'NXTMotor' in read:
       permissions.append('android.permission.BLUETOOTH')
   if config.getConversionMode() == ConversionMode.LIVE_WALLPAPER:
       permissions.append('android.permissions.STATE') #TODO: correct that

   config.setPermissions(permissions)
   if config.isVerbose():
       print "Neccessary permissions: ", permissions

def copyApkToOutputFolder(working_dir, project_filename, output_dir):
    for filename in os.listdir(os.path.join(working_dir, 'catroid', 'bin')):
        if filename.endswith('.apk'):
            shutil.move(os.path.join(working_dir, 'catroid', 'bin', filename),\
                    os.path.join(output_dir, project_filename + '.apk'))

def printConfig(config_to_print):
    print "Starting Native App Converter"
    print "------------------------------"
    print "Script starting with following params:"
    print "Path to project: ", config_to_print.getFullProjectPath()
    print "Project filename: ", config_to_print.getProjectFilename()
    print "Conversion Mode: ", config_to_print.getConversionMode() 
    print "Project ID: ", config_to_print.getProjectId()
    print "Working directory: ", config_to_print.getWorkingDir()
    print "Output folder: ", config_to_print.getPathToOutputDirectory()
    print "------------------------------"

def main():
    parser = argparse.ArgumentParser(description="Catrobat NativeApp Converter")
    parser.add_argument("-v", "--verbose", action="store_true")
    convert_mode_group = parser.add_mutually_exclusive_group(required=True)
    convert_mode_group.add_argument("-n", "--native-app", action="store_true",
            help="Convert to native app")
    convert_mode_group.add_argument("-w", "--live-wallpaper",
    action="store_true", help="Convert to live wallpaper")
    parser.add_argument('project', help="path to project archive that should be to converted")
    parser.add_argument('catrobatsrc', help="path to catroid sources")
    parser.add_argument('lib_src', help="path to lib folder")
    parser.add_argument('project_id', type=int, help='project id')
    parser.add_argument('output_dir', help="path to output folder")
    args = parser.parse_args()


    config = ConversionConfig(args)
    if config.isVerbose() == True:
        printConfig(config)


    unzip_project(config.getFullProjectPath(), config.getWorkingDir())
    project_name = get_project_name(config.getPathToProjectCode())
    acquireNecessaryPermissions(config)
    rename_resources(config.getWorkingDir(), config.getProjectFilename())
    copy_project(config.getPathToCatroidSrc(), config.getWorkingDir())
    shutil.copytree(config.getLibPath(), os.path.join(config.getWorkingDir(), 'libraryProjects'))

#Update build.xml files / Generate if not available.
    with open(os.devnull, 'wb') as devnull:
        if config.isVerbose():
            output_redir = None
        else:
            output_redir = devnull

        subprocess.check_call(['android', 'update', 'project', '-p',
            os.path.join(config.getWorkingDir(), 'catroid')],
            stdout=output_redir)
        subprocess.check_call(['android', 'update', 'lib-project', '-p',
            os.path.join(config.getWorkingDir(), 'libraryProjects/actionbarsherlock')],
            stdout=output_redir)

    if os.path.exists(os.path.join(config.getWorkingDir(), 'catroid', 'gen')):
        shutil.rmtree(os.path.join(config.getWorkingDir(), 'catroid', 'gen'))

    editManifest(config)

    rename_package(config.getWorkingDir(), 'app_' + str(config.getProjectId()))

    language_dirs = glob.glob(os.path.join(config.getWorkingDir(), 'catroid', 'res', 'values*'))
    for curr_lang_dir in language_dirs:
        editing_file = os.path.join(curr_lang_dir, 'strings.xml')
        if os.path.exists(editing_file):
            set_project_name(project_name, editing_file)

    with open(os.devnull, 'wb') as devnull:
        if config.isVerbose():
            output_redir = None
        else:
            output_redir = devnull
        subprocess.check_call(['ant', ANT_BUILD_TARGET ,'-f',
            os.path.join(config.getWorkingDir(), 'catroid', 'build.xml')],
            stdout=output_redir)

    copyApkToOutputFolder(config.getWorkingDir(), config.getProjectFilename(),
            config.getPathToOutputDirectory())

    shutil.rmtree(config.getWorkingDir())
    return 0

if __name__ == '__main__':
    main()
