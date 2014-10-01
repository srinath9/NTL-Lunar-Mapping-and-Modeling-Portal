imagePath=$1
scriptPath=$3
mapPath=$4

#############

echo "$@ task id $mapred_task_id" >> /tmp/params

me=$mapred_task_id
ids=$2
suffix='_0$'
if [ `echo $me | egrep $suffix` ];then
echo $me
else
echo `date` exiting >> /tmp/status
exit 0
fi
echo 1 >> /tmp/status
path='/tmp/'$me'_pic'
echo 2 path $path >> /tmp/status
output='/tmp/'$ids'_final'
echo 3 output $output  >> /tmp/status
rm -fr $path
mkdir -p $path
mkdir -p $output
echo 4 >> /tmp/status
while read line
do
if [ ${line##*.} = IMG ]; then
cp $imagePath/$line $path/$line
fi
done
echo 5 sh $scriptPath $path $output $mapPath  >> /tmp/status
sh $scriptPath $path $output $mapPath

## This code is needed for clustering
rm -fr $output/*.cub
ssh master0 "mkdir $output"
scp $output/* master0:$output
rm -fr $output
rm -fr $path
echo 'OK'
