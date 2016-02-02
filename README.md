## Jenkins APTLY repository management solution (sort of)
 
If you use jenkins with debian-package-builder, you may want to keep a local apt repository.
Running aptly manually as a build step after the actual build works, but only when you are running 
everything on ONE master node. When you have a bunch of equivalent nodes, you run into a problem:
You want to keep the repository and aptly on just ONE node and do the actual builds on a bunch of nodes. 
 
This plugin aims to resolve this problem. It adds a build step that allows you to add any .debs from project
artifacts into ONE master repository. The current implementation is VERY naive, and it assumes that you have 
aptly running on master node. 

## Setting up other required stuff.

In short: 

* Read aptly docs: http://www.aptly.info/
* sudo su jenkins and set up things on behalf of jenkins
* Add /var/lib/jenkins/.aptly/public to your webserver www path

### Building and installing jenkins plugin

If you want to build this package you'll need to have JDK 6 and maven2 installed, and the installation process depends on your OS and package manager. For example, Debian + aptitude users can do:

    sudo apt-get install default-jdk maven2

Clone the git repository and build the package

    git clone https://github.com/nekromant/jenkins-aptly-plugin.git 
    cd memegen
    mvn package

This will generate an hpi file at `target/aptly.hpi`. This needs to be copied to the Jenkins plugin directory. If a version of this plugin has already been installed, run

    rm -rf /var/lib/jenkins/plugins/aptly*

to get rid of it. Then either use the advanced tab of the plugin manager to upload the hpi file or copy it to the plugins directory, e.g. 

    cp target/aptly.hpi /var/lib/jenkins/plugins/

Finally, restart jenkins.

Alternatively, you can use jenkins-cli.jar:

    java -jar jenkins-cli.jar -s http://localhost:8080  install-plugin target/*.hpi
    java -jar jenkins-cli.jar -s http://localhost:8080  restart


### TODO

    * GUI for creating managing aptly repo and publishing stuff
    * Build step to republish repository
    * See if the need of external webserver can be ditched in favor of something usual
    * More sanity checking
    * Add ability to specify a node hosting aptly repositoy (Is it really needed?)

### Why does the code look so weird in line XXX?

Folks, I don't know java and crunched this plugin in a few days picking up all the required
stuff on the way. Code reviews and comments welcome.


### License

Copyright &copy; 2016, Andrew 'Necromant' Andrianov. Licensed under the [Apache 2 License].

[Apache 2 License]: https://github.com/nekromant/jenkins-aptly-plugin/raw/master/LICENSE.txt
