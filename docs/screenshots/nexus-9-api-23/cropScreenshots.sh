#!/bin/bash

# Requires ImageMagick

# New height
croppedHeight=1952
for file in *.png; do
    width=$(identify -format '%w' ${file})
    height=$(identify -format '%h' ${file})
    # Only crop uncropped images
    if [ ${width} == 1536 ] && [ ${height} == 2048 ]; then
        convert ${file} -crop ${width}x${croppedHeight}+0+0 ${file}
        printf "Cropped %s\n" ${file}
    fi
done
