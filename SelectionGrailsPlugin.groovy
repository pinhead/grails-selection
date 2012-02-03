/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
import grails.spring.BeanBuilder
import grails.plugins.selection.SelectionArtefactHandler
import grails.plugins.selection.GrailsSelectionClass

class SelectionGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/domain/**"
    ]
    def loadAfter = ['logging']
    def watchedResources = [
            "file:./grails-app/selection/**/*Selection.groovy",
            "file:./plugins/*/grails-app/selection/**/*Selection.groovy"
    ]
    def artefacts = [new SelectionArtefactHandler()]

    def title = "Selection Plugin" // Headline display name of the plugin
    def author = "Goran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = '''\
The selection plugin provides unified selection of information.
It uses a URI based syntax to select any information from any resource.
Grails plugins can add custom search providers.
Example 1: gorm://person/list?name=Gr%
Example 2: ldap:dc=my-company&dc=com&cn=users
Example 3: bean:myService.method
Example 4: http://api.my-company.com/rest/events?system=42
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/selection"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]

    // Any additional developers beyond the author specified above.
    //    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: "JIRA", url: "http://jira.grails.org/browse/GPSELECTION"]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/goeh/grails-selection"]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // Configure selection handlers
        def selectionClasses = application.selectionClasses
        selectionClasses.each { selectionClass ->
            "${selectionClass.propertyName}"(selectionClass.clazz) { bean ->
                bean.autowire = "byName"
            }
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
        println "Installed selection handlers ${application.selectionClasses*.propertyName}"

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        if (application.isSelectionClass(event.source)) {
            log.debug "Selection ${event.source} modified!"

            def context = event.ctx
            if (!context) {
                log.debug("Application context not found - can't reload.")
                return
            }

            // Make sure the new selection class is registered.
            def selectionClass = application.addArtefact(GrailsSelectionClass.TYPE, event.source)

            // Create the selection bean.
            def bb = new BeanBuilder()
            bb.beans {
                "${selectionClass.propertyName}"(selectionClass.clazz) { bean ->
                    bean.autowire = "byName"
                }
            }
            bb.registerBeans(context)
        }
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

}