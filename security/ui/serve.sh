#!/bin/bash
p=${1:-8020}
d=${2:-$PWD}
cd $d
jwebserver -d $d -p $p

# echo $d 
# cd $d 
# python3 -m http.server $p
# # python  -m SimpleHTTPServer $p 
