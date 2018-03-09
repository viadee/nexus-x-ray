NEXUS_REPO=$1
KEYWORD=$2
cd $NEXUS_REPO
echo Search Keyword $KEYWORD in Nexus Blob-Store $NEXUS_REPO
PROP_FILES=`find . -name '*.properties'`
echo Search keyword...
PROP_FILES_KEY=`ack-grep -l $KEYWORD $PROP_FILES`
BYTE_FILES_KEY=
echo Find Byte files
for FILE in $PROP_FILES_KEY ; do
 BYTE_FILES_KEY="`dirname $FILE`/`basename $FILE .properties`.bytes $BYTE_FILES_KEY"
  echo "`dirname $FILE`/`basename $FILE .properties`.bytes"
done
du -ckh $BYTE_FILES_KEY
