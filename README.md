# StanfordNERNameTaggingWebservice
The input is one json string that contains hocr content(ocr result for document files). It will first construct a big string as the input for Stanford NER, which will tag the word that is either person name or company name, and then based the stanford ner result add new tags to json and then output the updated json
# Apiary API Document: http://docs.tagnameentitiesusingstanfordner.apiary.io/#
