# Nexus X-Ray

![alt text](img/usage_zeke.png)

This Project is to analyze an existing Sonatype Nexus Repository 
installation, to identify projects or artifacts with high disk usage. 

It comes with a command-line interface where the target paths are 
being passed. An output .csv is generated and can be visualized
with the given R diagram templates.

## Setup

Building this project requires Maven to be installed. Simply 
call `mvn install` to package and install a fat-jar that
can be used on the target machine.


## Dependencies

All dependencies for the CLI are given with the included `pom.xml`. 
You should install R Studio to create diagrams.


## Commitments
This application will remain under an open source licence indefinately.

We follow the [semantic versioning](semver.org) scheme (2.0.0).

In the sense of semantic versioning, the resulting XML and JSON outputs are the only public API provided here. We will keep these as stable as possible, in order to enable users to analyse and integrate results into the toolsets of their choice.

## Cooperation
Feel free to report issues, questions, ideas or patches. We are looking forward to it.


## Licenses
Currently we use the following third-party dependencies:
* Apache Commons: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* picocli: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* Logback: [Logback License](https://logback.qos.ch/license.html)
* SLF4J: [SLF4J License](https://www.slf4j.org/license.html)
* Mockito: [MIT License](https://opensource.org/licenses/mit-license.php)
* JUnit: [Eclipse Public License 1.0](https://junit.org/junit4/license.html)

### BSD 3-Clause License 

Copyright (c) 2018, viadee IT-Unternehmensberatung GmbH All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

