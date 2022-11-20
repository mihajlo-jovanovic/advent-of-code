#!/bin/bash

awk 'BEGIN{x=0} x+=($1-$1%3)/3-2;{print x}' input 
