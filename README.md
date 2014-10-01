
# NASA Lunar Mapping Challenge
-----------------------------

The NASA Lunar Reconnaissance Orbiter is returning a large number of images of the lunar surface. This challenge will provide new and optimized methods for processing filtering and reconstructing these images.


### Build Setup
---------------

Modify build.properties. Ensure that pds.home points to the directory in which the [PDS
project](https://github.com/topcoderinc/pds_projects) is deployed. 

The Maven project expects ${pds.dir}/import_and_persistence to exist and to contains the NASA PDS/LMMP API Update.

### DB Setup
-------------

Create a database in the target MySQL database and run the following create statements:

* ``` CREATE TABLE Job (uuid char(36) primary key, status varchar(20) not null, hadoop_job_id varchar(100), fail_reason varchar(200), criteria text not null, num_images int, created timestamp not null, finished timestamp null); ``` 

* ``` CREATE TABLE JobImageUrl (uuid char(36) not null, url text not null); ``` 

### Hadoop Env Setup
---------------------
These environment variables may be set in ~hadoop/.bash_profile -- REMEMBER TO source the .bash_profile file!

<table style="width: 100%">
            <tr>
              <th style="width: 10%">Name</th>
              <th>Description</th>
              <th>Example ? </th>
            </tr>
            <tr>
              <td>HADOOP_HOME</td>
              <td>This should point to the Hadoop installation directory</td>
              <td>/home/hadoop/product/hadoop-2.2.0</td>
            </tr>
            <tr>
              <td>HADOOP_CONF_DIR</td>
              <td>Points at the Hadoop configuration folder, it may (will) vary depending on your local setup.</td>
              <td>${HADOOP_HOME}/etc/hadoop</td>
            </tr>
</table>


### Java properties file setup
-------------------------------
File is at src/main/resources/lmmp.properties - all of them are required

<table style="width: 100%">
            <tr>
              <th style="width: 10%">Name</th>
              <th>Description</th>
            </tr>
            <tr>
              <td>s3.bucket-name</td>
              <td>Name of the Amazon S3 Bucket.</td>
            </tr>
            <tr>
              <td>s3.key</td>
              <td>Amazon AWS Account Credentials - Key</td>
            </tr>
            <tr>
              <td>s3.secret</td>
              <td>Amazon AWS Account Credentials - Secret</td>
            </tr>
            <tr>
              <td>jdbc.lmmp.*</td>
              <td>The following configuration are used by LMMP application to access MySQL database</td>
            </tr>
            <tr>
              <td>jdbc.lmmp.driver-class</td>
              <td>MySQL JDBC Driver class name (i.e. com.mysql.jdbc.Driver)</td>
            </tr>
            <tr>
              <td>jdbc.lmmp.url</td>
              <td>MySQL JDBC connection string (i.e. jdbc:mysql://localhost:3306/lmmp)</td>
            </tr>
            <tr>
              <td>jdbc.lmmp.username</td>
              <td>MySQL username</td>
            </tr>
            <tr>
              <td>jdbc.lmmp.password</td>
              <td>MySQL password</td>
            </tr>
            <tr>
              <td>jdbc.pds.*</td>
              <td>The following configuration are used by PDS application to access MySQL database.</td>
            </tr>
            <tr>
              <td>jdbc.pds.driver-class</td>
              <td>MySQL JDBC Driver class name (i.e. com.mysql.jdbc.Driver)</td>
            </tr>
            <tr>
              <td>jdbc.pds.url</td>
              <td>MySQL JDBC connection string (i.e. jdbc:mysql://localhost:3306/nasa_pds)</td>
            </tr>
            <tr>
              <td>jdbc.pds.username</td>
              <td>MySQL username</td>
            </tr>
            <tr>
              <td>jdbc.lmmp.password</td>
              <td>MySQL password</td>
            </tr>
            <tr>
              <td>hadoop.job.local-work-directory</td>
              <td>Each LmmpJob/Hadoop Job will store files under this diretory, while the job is running (e.g. job with _UUID_ will store files in ${hadoop.job.local-work-directory/_UUID_/somefile.<br>
              </td>
            </tr>
            <tr>
              <td>hadoop.custom-partitioner-jar-file</td>
              <td>A custom partitioner for processing images, the source file exists under ./CustomPartitioner folder.<br> This file should be deployed under '>hadoop.job.local-work-directory' diretory.<br> <a href="http://hadooptutorial.wikispaces.com/Custom+partitioner" target="_blank">Read more about Custom Partitioner</a></td>
            </tr>
            <tr>
              <td>hadoop.handle-script</td>
              <td>The path to shell script file prepare the folders and parameters for the run.sh shell script execution. It is included under src/main/sh/ folder, it's name is handle-parameterized.sh. <br> This file should be deployed under '>hadoop.job.local-work-directory' diretory.</td>
            </tr>
            <tr>
              <td>isis.run-script</td>
              <td>The path to run.sh script that handle the image processing and mosaicing. It is included under src/main/sh/ folder, it's name is run.sh <br> This file should be deployed under '>hadoop.job.local-work-directory' diretory.</td>
            </tr>
            <tr>
              <td>isis.moon-map</td>
              <td>A PVL formatted file contains the moon map projection, used by cam2map tool during run.sh file execution.<br> This file should be deployed under '>hadoop.job.local-work-directory' diretory.</td>
            </tr>
</table>


### Deploy Locally
-----------------

* (We assume that Java, MySQL, Hadoop, isis, and FWTools (gal libs) are installed on the VM you are deploying to)
* Update build.properties
* Create hadoop.job.local-work-directory folder
* Copy CustomPartitioner jar file to hadoop.job.local-work-directory folder
* Copy src/main/sh/run.sh to hadoop.job.local-work-directory folder
* Copy src/main/resources/moon.map file to hadoop.job.local-work-directory folder
* Copy src/main/sh/handle-parameterized.sh to hadoop.job.local-workd-directory folder
* Update the paths of the following variables in copied src/main/sh/run.sh

          # This should point at the installation directory of isis tool
          export ISISROOT=/home/isis/isis
          # Update this to point at correct path of FWTools directory
          export PATH=$ISISROOT/3rdParty/lib:/home/isis/FWTools-linux-x86_64-3.0.6f-usgs/bin_safe:$PATH
* Remove the following block from copied src/main/sh/handle-parameterized.sh file if you are using single-instance hadoop setup
        
          ## This code is needed for clustering
          rm -fr $output/*.cub
          ssh master0 "mkdir $output"
          scp $output/* master0:$output
          rm -fr $output
          rm -fr $path
          echo 'OK'
* Update src/main/resources/lmmp.properties
* Execute the following commands to deploy the folder to tomcat server

          mvn clean package
          cp target/lmmp-rest*.war /PATH/TO/DEPLOYMENT/FOLDER

* If you want to deploy in-place you can run this command

          mvn -Phadoop-tomcat tomcat:run -Dmaven.tomcat.port=8181
          
          or you can run as daemon 
   
          nohup ./run.sh &

### Verify Installation
--------------------

Open your browser to http://localhost:8181/lmmp-rest

## Cluster deployment notes
----------------------------

* Hadoop, ISIS, and FWTools (Gdal libs) should be installed in all nodes.
* Hadoop env setup mentioned above should be applied to all Hadoop nodes.
* Change master0 to the IP or domain name of the master hadoop node in copied src/main/sh/handle-parameterized.sh file
              ## This code is needed for clustering
              rm -fr $output/*.cub
              ssh master0 "mkdir $output" # this step will gurantee that folder in master node exists, it won't affect execution if folder already exists
              scp $output/* master0:$output # this step will copy the output folder form slave node to master node
              rm -fr $output
              rm -fr $path
              echo 'OK'
* Mount the _hadoop.job.local-work-directory_ in each slave node
  * ``` sudo apt-get install sshfs```
  * ``` rm -fr {hadoop.job.local-work-directory} ```
  * ``` mkdir {hadoop.job.local-work-directory} ```
  * ``` sshfs {user}@{master-node-ip}:{hadoop.job.local-work-directory} {hadoop.job.local-work-directory} ```
  * example
    * ``` rm -fr /home/hadoop/scripts ```
    * ``` mkdir /home/hadoop/scripts ```
    * ``` sshfs hadoop@master0:/home/hadoop/scripts /home/hadoop/scripts ```
* Start server on master node
  * ``` cd $HADOOP_HOME/sbin ```
  * ``` hdfs namenode -format ```
  * ``` start-all.sh ```
* To restart cluster
  * stop the cluster by   ``` $HADOOP_HOME/stop-all.sh  ``` 
  * delete the files under /homw/hadoop/product/hadoop-2.2.0/tmp of each VM
  * run   ```  hadoop namenode -format  ``` in Mater VM.
  * run   ```  $HADOOP_HOME/start-all.sh   ``` 






