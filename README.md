## Synopsis

This project contains the Android application for the Cycle X-Pro. This project can be directly cloned from Android Studio. The latest version of GraphView is needed.

## Log

### Version

2.6.3

### Most Recent Changes

2.6.3
- Send Name Tested
- Threat Indicator Tested
- ALS Done!

2.6.2
- Shared Preferences Updated
- BT now sends user name and weight

2.6.1
- Added Settings Activity

2.6.0
- Tested Race Mode
- Bluetooth buffer more stable
- Updated messaging protocol

2.5.7
- Added Race Mode - untested

2.5.6
- Threat Indicator added, untested
- Updated BT receive algorithm

2.5.5
- Tested Battery Indicator
- Rewrote bad reads algorithm

2.5.4
- Enabled and Tested SMS Manager.
- END_SESSION Command after ERPS ends

2.5.3
- Fixed ERPS and Timer Issues

2.5.2
- Idle/battery mode added
- Not receiving ERPS messages

2.5.1
- ERPS activity and functionality added. Untested.

2.5.0
- The App was updated to parse the CXP 2.0.0 data format and protocols

## Code Design

There are some JAVA (non-activity) classes: Constants, Globals, DataLogger, DataPusher, and CustomHandler. The rest are activities.

### Constants.java 

This java class contains final static variables (read-only) and are shared between the processes

### Globals.java

This java class contains static variables (read and write). These are essentially global variables and are carefully used.

### DataLogger.java

This java class executes a thread to write values to a locally stored CSV file.

### DataPusher.java

This java class converts CSV to JSON and the performs an HTTP POST request.

### CustomHandler.java

This is thread that allows for interprocess communication. This is safer alternative to unwanted global variables.

### BluetoothActivity.java

This activity iniitiates Bluetooth connection. It alco contains the BT communication thread.

### MetricsActivity.java

This activity is where most of the magic happens. This activity displays data as it is streamed.

### ERPSActivity.java

This activity is created when ERPS is activated from the MCU. At the end of timer, an SMS is sent.

## License

This file is licensed under MIT
 
The MIT License (MIT)
 
Copyright (C) 2016 Carlos Salamanca (@iamsitting)
 
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy modify, merge publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTIBILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.