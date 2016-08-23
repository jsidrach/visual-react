#!/bin/bash

# Requires ImageMagick

# New height
croppedHeight=1794
for file in *.png; do
    width=$(identify -format '%w' ${file})
    height=$(identify -format '%h' ${file})
    # Only crop uncropped images
    if [ ${width} == 1080 ] && [ ${height} == 1920 ]; then
        convert ${file} -crop ${width}x${croppedHeight}+0+0 ${file}
        printf "Cropped %s\n" ${file}
    fi
done
