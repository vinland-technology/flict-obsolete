#!/bin/sh

if [ "$1" != "" ]
then
    OUT_FILE=$(echo $1 | sed 's,\.json,-fixed\.json,'g)
fi

fix()
{
    cat $1  |
        sed -e 's,BSD,BSD-3-Clause,g' -e 's,bzip2,MIT,g' | \
            sed -e 's,+,-or-later,g' -e 's,v2,-2,g'  -e 's,-2-,-2.0-,g' -e 's,LGPL-2.1,LGPL-2.1-or-later,' | \
            sed -e 's,MIT-style,MIT,g' -e 's,FreeType,MIT,g'|\
            sed -e 's,MPL\-1,MPL\-1\.1,g' 
            
}
    
if [ "$OUT_FILE" != "" ]
then
    fix $1 > $OUT_FILE
else
    fix $1
fi
