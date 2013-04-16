#!/bin/sh
#PBS -N ${RUN_NAME}
#PBS -l procs=${PROCS},walltime=${TIME},qos=${QOS}
#PBS -l pmem=1024mb
#PBS -q ${QUEUE}
#${MAIL_FLAG}
#PBS -V
#${ACCOUNT}
#${MAIL}

cd ${RUN_DIR}

java -Xmx1000m -cp ${CLASS_PATH} com.sasbury.genetik.Genetik run.properties

exit 0