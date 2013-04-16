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
cd ..
touch lastjob

exit 0
