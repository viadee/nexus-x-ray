#!/bin/bash
cat header.txt | sed 's/^/"@|fg(5;4;2) /g' | sed 's/$/|@",/g' | sed 's/\\/\\\\/g'