#!/usr/bin/env bash

file="$1"
volume_norm_level="$(ffmpeg -i "$file" -af volumedetect -f null /dev/null 2>&1 | grep max_volume | awk '{print $5}' | sed s/-//)"
ffmpeg -i "$file" -af "volume=(-15dB * ${volume_norm_level}dB),silenceremove=1:0:-40dB:1:5:-40dB:0" -f wav -ac 1 pipe: | ffplay -autoexit -nodisp -f wav pipe: