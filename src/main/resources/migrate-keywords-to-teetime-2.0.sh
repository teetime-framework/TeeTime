#!/bin/bash

function replace(){
echo $1
sed -i -- '/onInitializing/d' $1
sed -i -- 's:connectInterThreads:connectPorts:' $1
sed -i -- 's:connectIntraThreads:connectPorts:' $1
sed -i -- 's:AnalysisConfiguration.connectPorts:connectPorts:' $1
sed -i -- 's:addThreadableStage:declareActive:' $1
sed -i -- 's: AnalysisConfiguration: Configuration:' $1
sed -i -- 's: AnalysisConfiguration: Configuration:' $1
sed -i -- 's:import teetime.framework.AnalysisConfiguration:import teetime.framework.Configuration:' $1
sed -i -- 's: Analysis: Execution:' $1
sed -i -- 's: Analysis: Execution:' $1
sed -i -- 's:import teetime.framework.Analysis:import teetime.framework.Execution:' $1
sed -i -- 's:import teetime.stage.basic.distributor.CopyByReferenceStrategy:import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy:' $1

}

echo "Migrating to TeeTime 2.0"

export -f replace
find $1 -type f -name *.java -exec bash -c 'replace "$0"' {} \;

echo "Done"