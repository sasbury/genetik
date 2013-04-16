#!/bin/sh
#PBS -N ${RUN_NAME}
#PBS -l nodes=${NODES}:ppn=${PPN},walltime=${TIME},qos=${QOS}
#PBS -q ${QUEUE}
#${MAIL_FLAG}
#PBS -V
#${ACCOUNT}
#${DEPENDS}
#${MAIL}

cd ${RUN_DIR}

java -cp ${CLASS_PATH} com.sasbury.genetik.driver.cluster.jobs.ClusterJob ${JOB_TYPE} generation=${GENERATION} chunk=${CHUNK}

exit 0
