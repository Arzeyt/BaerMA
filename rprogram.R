sampleGenerationList <- read.table("C:/Users/Nick/Google Drive/IdeaProjects/BaerMA/calculated generation list.txt", header=TRUE,sep=",")
data(sleep)

sleep
dim(sleep)
str(sleep)
summary(sleep)
sampleGenerationList

library(readr)

setwd("C:/Users/Nick/Google Drive/IdeaProjects/BaerMA/")
write_csv(sampleGenerationList,"sgl.txt")

sampleGenerationList$newColumn <- sampleGenerationList$Generation

head
