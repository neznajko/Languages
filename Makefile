#[40]###################################
PROJ = Languages
LDIR = lib
TARG = ${LDIR}/${PROJ}.class
FLGS = -g -Xlint:deprecation

.PHONY: clean run 

${TARG}: ${PROJ}.java
	javac ${FLGS} -d ${LDIR} -cp ${LDIR} $<

clean:
	${RM} ${wildcard */*.class}

run:
	@make
	java -cp ${LDIR} ${PROJ} ${ARGS}
#[40]############################## log:
