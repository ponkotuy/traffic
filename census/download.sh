#!/bin/sh

wget http://www.mlit.go.jp/road/census/h27/data/csv/kasyo{01..47}.csv
find . -name "*.csv" -exec nkf -w --overwrite {} \;
