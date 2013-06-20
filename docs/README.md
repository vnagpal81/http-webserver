			 			 ============================
                         Domgatix 1.0 HTTP Web Server
                         ============================

A limited use HTTP webserver intended as a pure academic exercise.

The documentation heavily borrows the structure and language from the Apache Tomcat's 
own docs while the implementation borrows concepts and designs and at a couple of places 
even source code from various open source libraries especially Apache httpd and Spring framework.

The nomenclature Dogmatix is based on the canine character in the comix (sic) Asterix attributing 
the server's capabilities to that of a loyal dog's.

* For downloading the source and building the binary, see docs/BUILDING.txt
* For configuring the behaviour of a runtime server instance, see docs/CONFIGURING.txt
* For launching/stalling the server and other runtime options, see docs/RUNNING.txt
* For extending the server capabilities by editing the source, see docs/EXTENDING.txt
* To check the current dev status of the project and list of TODOs, see docs/STATUS.txt

============
Inspirations
============

Following links were referred to again and again during the development cycle:

http://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java

http://www.javacodegeeks.com/2011/01/10-tips-proper-application-logging.html

http://www.slf4j.org/manual.html

http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html

http://sourcemaking.com/design_patterns/object_pool/java

http://code.google.com/p/reflections/

http://www.java2s.com/Code/Java/Network-Protocol/HttpParser.htm

http://sourceforge.net/projects/jmimemagic/

http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html

http://www.artima.com/designtechniques/threadsafety.html


Apart from these, the source code wherever it is borrowed, has been attributed so in the docs.