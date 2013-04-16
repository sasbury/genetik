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

#just cd - that is enough for a join job

exit 0
