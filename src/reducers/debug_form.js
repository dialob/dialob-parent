
const DEBUG_FORM = {
  "_id": "47e26c52d2954be5b11028fc0b5caf38",
  "_rev": "1444-a38491432e26323d1d219d45c3a32c66",
  "data": {
      "otherBonds": {
          "id": "otherBonds",
          "type": "number",
          "label": {
              "en": "Joukkovelkakirjalainat?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group12": {
          "id": "group12",
          "type": "group",
          "label": {
              "en": "Pankin tiedot"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "question30"
          ],
          "className": [
              "hidden-print"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherFStock": {
          "id": "otherFStock",
          "type": "number",
          "label": {
              "en": "Osakerahastot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "warrantsKnow": {
          "id": "warrantsKnow",
          "type": "boolean",
          "label": {
              "en": "Warrantit"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group13": {
          "id": "group13",
          "type": "group",
          "label": {
              "en": "Varallisuus yhteenveto"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "question1",
              "question2",
              "question17"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "clientInvestPeriod": {
          "id": "clientInvestPeriod",
          "type": "list",
          "label": {
              "en": "Valitse sinulle sopiva sijoitusaika vuosissa?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "clientInvestPeriod_valueset2",
          "defaultValue": null
      },
      "lastName": {
          "id": "lastName",
          "type": "text",
          "label": {
              "en": "Sukunimi"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSEurooppa": {
          "id": "popSEurooppa",
          "type": "number",
          "label": {
              "en": "POP Eurooppa?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "stockKnow": {
          "id": "stockKnow",
          "type": "boolean",
          "label": {
              "en": "Pörssiosakkeet"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popS": {
          "id": "popS",
          "type": "boolean",
          "label": {
              "en": "Säästövakuutukset (rahastosidonnaiset)?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": ""
                  },
                  "rule": ""
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popOtherA": {
          "id": "popOtherA",
          "type": "boolean",
          "label": {
              "en": "Muuta varallisuutta POP pankissa?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group14": {
          "id": "group14",
          "type": "group",
          "label": {
              "en": "Rahastot"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "popF",
              "popFKorkoSalkku",
              "popFVakaa",
              "popFOptimi",
              "popFMaailma",
              "popFEurooppa",
              "popFPohjoismaat",
              "popFSuomi",
              "popFShortInterest",
              "popFLongInterest",
              "popFOtherWealth",
              "popFOtherStock",
              "popFOther"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSMaailma": {
          "id": "popSMaailma",
          "type": "number",
          "label": {
              "en": "POP Maailma?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "derivatesKnowFreq": {
          "id": "derivatesKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "derivatesKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset1121",
          "defaultValue": null
      },
      "page1": {
          "id": "page1",
          "type": "group",
          "label": {
              "en": "Aloitussivu"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "group18",
              "group12",
              "group1",
              "group2",
              "group3"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherProperties": {
          "id": "otherProperties",
          "type": "number",
          "label": {
              "en": "Sijoituskiinteistöt?"
          },
          "description": {
              "en": "Sijoitusasunto- ja liikehuoneisto-osakkeet"
          },
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "question30": {
          "id": "question30",
          "type": "note",
          "label": {
              "en": "#### Pankki konttorin tunnus: {org}\n\n#### Toimihenkilön nimi ja tunnus: {sub} {aud}\n\n#### MiFIDII arvoinnin liittyvän henkilön tunniste: {hetu}\n"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group15": {
          "id": "group15",
          "type": "group",
          "label": {
              "en": "Säästövakuutukset"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "popS",
              "popSKorkoSalkku",
              "popSVakaa",
              "popSOptimi",
              "popSMaailma",
              "popSEurooppa",
              "popSPohjoismaat",
              "popSSuomi",
              "popSSalkku1",
              "popSSalkku2",
              "popSSalkku3",
              "popSSalkku4",
              "popSSalkku5",
              "popSShortInterest",
              "popSLongInterest",
              "popSOtherStock",
              "popSOtherWealth",
              "popSOther"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherSShortInterest": {
          "id": "otherSShortInterest",
          "type": "number",
          "label": {
              "en": "Lyhyen korkon rahasto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "questionnaire": {
          "id": "questionnaire",
          "type": "questionnaire",
          "label": {
              "en": "New Dialog"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "page1",
              "page2",
              "page3",
              "page4"
          ],
          "className": [
          ],
          "activeWhen": null,
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "workStatus": {
          "id": "workStatus",
          "type": "list",
          "label": {
              "en": "Työtilanne"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question8_valueset1",
          "defaultValue": null
      },
      "otherFWealth": {
          "id": "otherFWealth",
          "type": "number",
          "label": {
              "en": "Muu reaali omaisuus?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherS": {
          "id": "otherS",
          "type": "boolean",
          "label": {
              "en": "Säästövakuutuksia muualla?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": ""
      },
      "page2": {
          "id": "page2",
          "type": "group",
          "label": {
              "en": "Asiakkaan varallisuus"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "group4",
              "group14",
              "group15",
              "group16",
              "group41",
              "group8",
              "group19",
              "group20",
              "group17",
              "group13"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group16": {
          "id": "group16",
          "type": "group",
          "label": {
              "en": "Asiakkaan muu varallisuus POP pankissa"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "popOtherA",
              "popProperties",
              "popOsuus",
              "popBonds",
              "popInsuranceSavings",
              "popPensionSavings",
              "popStock",
              "popDerivatives",
              "popWaranties",
              "popStructuredProducts",
              "popPankAdditionalShares"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "clientInvestmentTargets": {
          "id": "clientInvestmentTargets",
          "type": "multichoice",
          "label": {
              "en": "Valitse sinulle sopivat"
          },
          "description": {
              "en": null
          },
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question43_valueset1",
          "defaultValue": null
      },
      "popProperties": {
          "id": "popProperties",
          "type": "number",
          "label": {
              "en": "Sijoituskiinteistöt?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popSShortInterest": {
          "id": "popSShortInterest",
          "type": "number",
          "label": {
              "en": "Muu lyhyt korko?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "page3": {
          "id": "page3",
          "type": "group",
          "label": {
              "en": "Ymmärrys sijoittamisesta"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "group5",
              "group6"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group17": {
          "id": "group17",
          "type": "group",
          "label": {
              "en": "Sijoitusvarallisuuteen liittyvä velka"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "loansAgainstTotalWealth"
          ],
          "className": [
          ],
          "activeWhen": "popOtherA = true or outsidePop = true or popS = true or popF = true",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherStructuredProducts": {
          "id": "otherStructuredProducts",
          "type": "number",
          "label": {
              "en": "Strukturoidut tuotteet kuten indeksilainat?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "investKnowlidge": {
          "id": "investKnowlidge",
          "type": "multichoice",
          "label": {
              "en": "Asiakkaan entuudesta tuntemat sijoituspalvelut"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question31_valueset1",
          "defaultValue": null
      },
      "remainingHomeLoan": {
          "id": "remainingHomeLoan",
          "type": "number",
          "label": {
              "en": "Kuinka paljon velkaa on vielä jäljellä?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "livingStatus = 'a1'",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popStock": {
          "id": "popStock",
          "type": "number",
          "label": {
              "en": "Osakkeet?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "page4": {
          "id": "page4",
          "type": "group",
          "label": {
              "en": "Asiakkaan sijoitustavoitteet"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "group7",
              "group11",
              "group9",
              "group10"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "question11": {
          "id": "question11",
          "type": "note",
          "label": {
              "en": "##### Yhteenveto asiakaan kuukausittaisista säännollisistä tuloista ja menoista\n\n##### Säännölliset kuukausittaiset tulot yhteensä {totalMonthlyIncome} €\n\n##### Säännölliset kuukausittaiset menot yhteensä {totalMonthlyExpens} €\n\n##### Kuukausittaisten tulojen ja menojen välinen erotus/ säästövara {netMonthlyIncome} €\n\n#### ​\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-1"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group18": {
          "id": "group18",
          "type": "group",
          "label": {
              "en": "Sijoitusneuvonta tausta"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "question3"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popStructuredProducts": {
          "id": "popStructuredProducts",
          "type": "number",
          "label": {
              "en": "Strukturoidut tuotteet kuten indeksilainat?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "strucPrdKnowFreq": {
          "id": "strucPrdKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "strucPrdKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset1121111",
          "defaultValue": null
      },
      "otherMonthlyIncome": {
          "id": "otherMonthlyIncome",
          "type": "number",
          "label": {
              "en": "Muut säännölliset kuukausittaiset tuet nettona?"
          },
          "description": {
              "en": null
          },
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "bondKnowFreq": {
          "id": "bondKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "bondKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset112",
          "defaultValue": null
      },
      "popFOptimi": {
          "id": "popFOptimi",
          "type": "number",
          "label": {
              "en": "POP Optimi?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group19": {
          "id": "group19",
          "type": "group",
          "label": {
              "en": "Säästövakuutukset muualla"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "otherS",
              "otherSShortInterest",
              "otherSLongInterest",
              "otherSStock",
              "otherSWealth",
              "otherSFund"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "outsidePop = true",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSKorkoSalkku": {
          "id": "popSKorkoSalkku",
          "type": "number",
          "label": {
              "en": "POP Korkosalkku?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popWaranties": {
          "id": "popWaranties",
          "type": "number",
          "label": {
              "en": "Warrantit?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popSSuomi": {
          "id": "popSSuomi",
          "type": "number",
          "label": {
              "en": "POP Suomi?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherSFund": {
          "id": "otherSFund",
          "type": "number",
          "label": {
              "en": "Muu"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popSavings": {
          "id": "popSavings",
          "type": "number",
          "label": {
              "en": "Talletukset euroissa?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "fundKnowFreq": {
          "id": "fundKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "fundKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset111",
          "defaultValue": null
      },
      "popSLongInterest": {
          "id": "popSLongInterest",
          "type": "number",
          "label": {
              "en": "Muu pitkä korko?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFOtherWealth": {
          "id": "popFOtherWealth",
          "type": "number",
          "label": {
              "en": "Muu reaaliomaisuusrahasto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popSOtherStock": {
          "id": "popSOtherStock",
          "type": "number",
          "label": {
              "en": "Muu Osake?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "education": {
          "id": "education",
          "type": "list",
          "label": {
              "en": "Koulutustaso"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question5_valueset1",
          "defaultValue": null
      },
      "otherSLongInterest": {
          "id": "otherSLongInterest",
          "type": "number",
          "label": {
              "en": "Pitkänkoron rahastot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherSavings": {
          "id": "otherSavings",
          "type": "number",
          "label": {
              "en": "Talletukset?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "outsidePop = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFPohjoismaat": {
          "id": "popFPohjoismaat",
          "type": "number",
          "label": {
              "en": "POP Pohjoismaat?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "derivatesKnow": {
          "id": "derivatesKnow",
          "type": "boolean",
          "label": {
              "en": "Johdannaiset"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherWarranties": {
          "id": "otherWarranties",
          "type": "number",
          "label": {
              "en": "Warrantit?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFVakaa": {
          "id": "popFVakaa",
          "type": "number",
          "label": {
              "en": "POP Vakaa?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "question1": {
          "id": "question1",
          "type": "note",
          "label": {
              "en": "##### Talletukset POP Pankissa: {popSavings} €\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherLivingStatus": {
          "id": "otherLivingStatus",
          "type": "text",
          "label": {
              "en": "Mikä muu asuinmuoto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "livingStatus = 'a5'",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "question17": {
          "id": "question17",
          "type": "note",
          "label": {
              "en": "##### Kokonaisvarallisuus muualla kuin POP Pankissa: {totalOtherAssets} €\n\n##### Asiakkaan kokonaisvarallisuus: {totalAssests} €\n\n##### Varallisuuteen sitoutunut kokonaisvelka: {loansAgainstTotalWealth} €\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "question2": {
          "id": "question2",
          "type": "note",
          "label": {
              "en": "##### Sijotukset POP Rahastohin: {totalFundsPOP} €\n\n##### Sijotukset POP Säästövakuutuksiin: {totalSavingsPOP} €\n\n##### Varallisuus yhteensä POP Pankissa: {totalPOP} €\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "outsidePop": {
          "id": "outsidePop",
          "type": "boolean",
          "label": {
              "en": "Onko asiakkaalla muualla kun POP pankissa merkittävää omaisuutta?"
          },
          "description": {
              "en": "Jotta POP pankki kykenee toteuttaamaan viranomaisten vaatiman MiFID II riskianalyysin se joutuu selvittämään asiakaan kokonasvarallisuuden."
          },
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "question18": {
          "id": "question18",
          "type": "note",
          "label": {
              "en": "[[\"https://flexiformcdn.blob.core.windows.net/pop-dialob-fill/risk-images/risk-images/image{riskAttitude:key}.png\"]]\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "imageurl"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "question3": {
          "id": "question3",
          "type": "note",
          "label": {
              "en": "Pankin on sijoituspalvelulain ja Finanssivalvonnan määräysten ja ohjeiden edellyttämällä tavalla hankittava soveltuvuusarvioinnin tekemistä varten riittävät tiedot asiakkaan tai asiakkaan edustajan sijoituskokemuksesta ja -tietämyksestä, asiakkaan taloudellisesta asemasta ja sijoitustavoitteista, jotta Pankki voi suositella asiakkaalle soveltuvia rahoitusvälineitä ja palveluita.\n\nSoveltuvuusarvioinnin tarkoituksena on antaa Pankille mahdollisuus toimia asiakkaan etujen mukaisesti. Tämän vuoksi on tärkeää, että asiakas antaa lomakkeessa ajantasaiset ja oikeat tiedot. Asiakas sitoutuu ilmoittamaan Pankille antamissaan tiedoissa tapahtuvista olennaisista muutoksista.\n\nMikäli kaikkia tässä lomakkeessa pyydettyjä tietoja ei anneta, Pankki ei voi tehdä asiakkaalle sijoituspalvelulain mukaista soveltuvuusarviota asiakkaalle soveltuvista rahoitusvälineistä ja palveluista.\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "attitudeLoss": {
          "id": "attitudeLoss",
          "type": "list",
          "label": {
              "en": "Jos tietyn sijoittamisen arvo pienenisi enemmän kuin olisin odottanut"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question44_valueset1",
          "defaultValue": null
      },
      "popSOther": {
          "id": "popSOther",
          "type": "number",
          "label": {
              "en": "Muu?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "question4": {
          "id": "question4",
          "type": "note",
          "label": {
              "en": "##### Kuukausittainen käyttövara/ tulojen ja menojen erotus: {netMonthlyIncome} €\n\n##### Talletukset POP Pankissa: {popSavings} €\n\n##### Sijoitukset POP Rahastoihin: {totalFundsPOP} €\n\n##### Sijoitukset POP Säästövakuutuksiin: {totalSavingsPOP} €\n\n##### Varallisuus yhteensä POP Pankissa: {totalPOP} €\n\n​\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "hidden-print"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "strucPrdKnow": {
          "id": "strucPrdKnow",
          "type": "boolean",
          "label": {
              "en": "Strukturoidut tuotteet (esim. indeksilainat)"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "capitalMonthlyIncome": {
          "id": "capitalMonthlyIncome",
          "type": "number",
          "label": {
              "en": "Säännölliset kuukausittaiset pääomatulot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "bondKnow": {
          "id": "bondKnow",
          "type": "boolean",
          "label": {
              "en": "Joukkovelkakirjalainat"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "question5": {
          "id": "question5",
          "type": "note",
          "label": {
              "en": "Säännöllisten tulojen ja menojen kautta POP pankki pystyy arviomaan varallisuuteen hoitoon käytettävissä olevan netto varallisuuden\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "instrumentsKnowFreq": {
          "id": "instrumentsKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "instrumentsKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset11211",
          "defaultValue": null
      },
      "normalMonthlyIncome": {
          "id": "normalMonthlyIncome",
          "type": "number",
          "label": {
              "en": "Säännölliset kuukausittaiset nettotulot (palkka tai eläke)?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFMaailma": {
          "id": "popFMaailma",
          "type": "number",
          "label": {
              "en": "POP Maailma?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherFShortInterest": {
          "id": "otherFShortInterest",
          "type": "number",
          "label": {
              "en": "Lyhyen korkon rahastot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherSWealth": {
          "id": "otherSWealth",
          "type": "number",
          "label": {
              "en": "Muu reaali omaisuus?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popInsuranceSavings": {
          "id": "popInsuranceSavings",
          "type": "number",
          "label": {
              "en": "Säästövakuutukset (laskuperustekorko)?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "question6": {
          "id": "question6",
          "type": "note",
          "label": {
              "en": "##### Kokonaisvarallisuus muualla kuin POP Pankissa: {totalOtherAssets} €\n\n##### Asiakkaan kokonaisvarallisuus: {totalAssests} €\n\n##### Varallisuuteen kohdistuva kokonaisvelka: {loansAgainstTotalWealth} €\n\n##### Varallisuuden allokaatio/omaisuusluokkajakauma tällä hetkellä:\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "fundKnow": {
          "id": "fundKnow",
          "type": "boolean",
          "label": {
              "en": "Rahastot"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "// question16 < 1000",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": "false"
      },
      "question7": {
          "id": "question7",
          "type": "note",
          "label": {
              "en": "[[\"#c07310\", \"Lyhyt korko\", \"{shortInterest:json}\"], [\"#c0f531\", \"Pitkä korko\", \"{longInterest:json}\"], [\"#b37ed8\", \"Osake\", \"{shares:json}\"], [\"#96c301\", \"Reaaliomaisuus\", \"{real:json}\"], [\"#3a01f6\", \"Vaihtoehtoinen\", \"{alternative:json}\"]]\n"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "piechart"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popFShortInterest": {
          "id": "popFShortInterest",
          "type": "number",
          "label": {
              "en": "Muu lyhyen koron rahasto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherA": {
          "id": "otherA",
          "type": "boolean",
          "label": {
              "en": "Muuta varallisuutta muualla?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group1": {
          "id": "group1",
          "type": "group",
          "label": {
              "en": "Asiakkaan perustiedot"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "firstNames",
              "lastName",
              "homeAddress"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "normalMonthlyLexpens": {
          "id": "normalMonthlyLexpens",
          "type": "number",
          "label": {
              "en": "Säännölliset kuukausittaiset asumismenot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "0 > answer"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherSStock": {
          "id": "otherSStock",
          "type": "number",
          "label": {
              "en": "Osake rahastot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group2": {
          "id": "group2",
          "type": "group",
          "label": {
              "en": "Koulutustaso ja elämäntilanne"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "education",
              "otherEducation",
              "maritialStatus",
              "workStatus",
              "otherWorkStatus",
              "children",
              "childrenUnderEighteen",
              "livingStatus",
              "otherLivingStatus",
              "remainingHomeLoan"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherWorkStatus": {
          "id": "otherWorkStatus",
          "type": "text",
          "label": {
              "en": "Mikä muu työtilanne?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "workStatus = 'workStatusOther'",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "loansAgainstTotalWealth": {
          "id": "loansAgainstTotalWealth",
          "type": "number",
          "label": {
              "en": "Yllä oleviin sijoituksiin liittyvä lainarahoitus?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFSuomi": {
          "id": "popFSuomi",
          "type": "number",
          "label": {
              "en": "POP Suomi?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group3": {
          "id": "group3",
          "type": "group",
          "label": {
              "en": "Asiakkaan taloudellinen tila"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "question5",
              "normalMonthlyIncome",
              "otherMonthlyIncome",
              "capitalMonthlyIncome",
              "normalMonthlyLexpens",
              "normalMonthlyOexpens",
              "otherMonthlyExpenses",
              "question11"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSOptimi": {
          "id": "popSOptimi",
          "type": "number",
          "label": {
              "en": "POP Optimi?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFKorkoSalkku": {
          "id": "popFKorkoSalkku",
          "type": "number",
          "label": {
              "en": "POP Korkosalkku?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFEurooppa": {
          "id": "popFEurooppa",
          "type": "number",
          "label": {
              "en": "POP Eurooppa?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popF": {
          "id": "popF",
          "type": "boolean",
          "label": {
              "en": "Rahasto-osuudet?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": ""
                  },
                  "rule": ""
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group4": {
          "id": "group4",
          "type": "group",
          "label": {
              "en": "Varallisuus POP Pankissa"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "popSavings"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "homeAddress": {
          "id": "homeAddress",
          "type": "text",
          "label": {
              "en": "Osoite ja postinumero"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-1"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherKnowType": {
          "id": "otherKnowType",
          "type": "text",
          "label": {
              "en": "Mikä muu palvelu"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "otherKnow = true",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group5": {
          "id": "group5",
          "type": "group",
          "label": {
              "en": "Asiakkaan sijoituskokemus"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "investExperience",
              "investKnowlidge",
              "clientInvestPeriod"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "normalMonthlyOexpens": {
          "id": "normalMonthlyOexpens",
          "type": "number",
          "label": {
              "en": "Säännölliset kuukausittaiset elämisen liittyvät kustannukset?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "0 > answer"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherF": {
          "id": "otherF",
          "type": "boolean",
          "label": {
              "en": "Rahasto osuudet?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "group6": {
          "id": "group6",
          "type": "group",
          "label": {
              "en": "Asiakkaan entuudestaan tuntemat palvelut"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "fundKnow",
              "fundKnowFreq",
              "stockKnow",
              "stockKnowFreq",
              "bondKnow",
              "bondKnowFreq",
              "derivatesKnow",
              "derivatesKnowFreq",
              "instrumentsKnow",
              "instrumentsKnowFreq",
              "warrantsKnow",
              "warrantsKnowFreq",
              "strucPrdKnow",
              "strucPrdKnowFreq",
              "otherKnow",
              "otherKnowType"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "instrumentsKnow": {
          "id": "instrumentsKnow",
          "type": "boolean",
          "label": {
              "en": "Rahamarkkinavälineet (alle vuoden mittaiset sijoitukset)"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "childrenUnderEighteen": {
          "id": "childrenUnderEighteen",
          "type": "number",
          "label": {
              "en": "Montako alaikäistä lasta?"
          },
          "description": {
              "en": "Tarkoitetaan alle 18-vuotiata lapsia, jotka asuvat samassa taloudessa!"
          },
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "children = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherMonthlyExpenses": {
          "id": "otherMonthlyExpenses",
          "type": "number",
          "label": {
              "en": "Muut kuukausittaiset kustannukset (harrastukset yms.)?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2",
              "euro"
          ],
          "activeWhen": "",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "0 > answer"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group7": {
          "id": "group7",
          "type": "group",
          "label": {
              "en": "Asiakkaan sijoitusajanjakso ja suhtautuminen sijoitusriskiin"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "riskAttitude"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSSalkku1": {
          "id": "popSSalkku1",
          "type": "number",
          "label": {
              "en": "POP Salkku 1?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "firstNames": {
          "id": "firstNames",
          "type": "text",
          "label": {
              "en": "Etunimet"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "otherEducation": {
          "id": "otherEducation",
          "type": "text",
          "label": {
              "en": "Mikä muu koulutustaso"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "education = 'educationOther'",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "riskAttitude": {
          "id": "riskAttitude",
          "type": "list",
          "label": {
              "en": "Valitse riskitaso ja tuotto-odotus"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question41_valueset1",
          "defaultValue": null
      },
      "stockKnowFreq": {
          "id": "stockKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "stockKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset11",
          "defaultValue": null
      },
      "group8": {
          "id": "group8",
          "type": "group",
          "label": {
              "en": "Rahasto osuudet muualla"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "otherF",
              "otherFShortInterest",
              "otherFLongInterest",
              "otherFStock",
              "otherFWealth",
              "otherFFund"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "outsidePop = true",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSSalkku2": {
          "id": "popSSalkku2",
          "type": "number",
          "label": {
              "en": "POP Salkku 2?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "warrantsKnowFreq": {
          "id": "warrantsKnowFreq",
          "type": "list",
          "label": {
              "en": "Kuinka usein olen käyttänyt palvelua joko itse tai jonkun välityksellä"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "warrantsKnow = true",
          "validations": [
          ],
          "valueSetId": "question42_valueset112111",
          "defaultValue": null
      },
      "group9": {
          "id": "group9",
          "type": "group",
          "label": {
              "en": "Sijoittamiseen ja säästämisen tarkoitus tai tavoite ja ajankohta"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "clientInvestmentTargets"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSSalkku3": {
          "id": "popSSalkku3",
          "type": "number",
          "label": {
              "en": "POP Salkku 3?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popPankAdditionalShares": {
          "id": "popPankAdditionalShares",
          "type": "number",
          "label": {
              "en": "POP Pankin lisäosuudet?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherPensionSavings": {
          "id": "otherPensionSavings",
          "type": "number",
          "label": {
              "en": "Eläkevakuutukset?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popDerivatives": {
          "id": "popDerivatives",
          "type": "number",
          "label": {
              "en": "Johdannaiset"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popSOtherWealth": {
          "id": "popSOtherWealth",
          "type": "number",
          "label": {
              "en": "Muu reaaliomaisuusrahasto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherKnow": {
          "id": "otherKnow",
          "type": "boolean",
          "label": {
              "en": "Muut"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "true",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSSalkku4": {
          "id": "popSSalkku4",
          "type": "number",
          "label": {
              "en": "POP Salkku 4?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFLongInterest": {
          "id": "popFLongInterest",
          "type": "number",
          "label": {
              "en": "Muu pitkän koron rahasto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherFFund": {
          "id": "otherFFund",
          "type": "number",
          "label": {
              "en": "Muu?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popFOtherStock": {
          "id": "popFOtherStock",
          "type": "number",
          "label": {
              "en": "Muu osakerahasto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "livingStatus": {
          "id": "livingStatus",
          "type": "list",
          "label": {
              "en": "Tämän hetkinen asumismuoto?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question10_valueset1",
          "defaultValue": null
      },
      "popFOther": {
          "id": "popFOther",
          "type": "number",
          "label": {
              "en": "Muu?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherFLongInterest": {
          "id": "otherFLongInterest",
          "type": "number",
          "label": {
              "en": "Pitkän koron rahastot?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherF = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group41": {
          "id": "group41",
          "type": "group",
          "label": {
              "en": "Varallisuus muualla"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "outsidePop",
              "otherSavings"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSSalkku5": {
          "id": "popSSalkku5",
          "type": "number",
          "label": {
              "en": "POP Salkku 5?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popPensionSavings": {
          "id": "popPensionSavings",
          "type": "number",
          "label": {
              "en": "Eläkevakuutukset?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "otherDerivatives": {
          "id": "otherDerivatives",
          "type": "number",
          "label": {
              "en": "Johdannaiset?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "otherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group20": {
          "id": "group20",
          "type": "group",
          "label": {
              "en": "Muu omaisuus"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "otherA",
              "otherProperties",
              "otherBonds",
              "otherPensionSavings",
              "otherDerivatives",
              "otherWarranties",
              "otherStructuredProducts"
          ],
          "className": [
              "row"
          ],
          "activeWhen": "outsidePop = true",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popSPohjoismaat": {
          "id": "popSPohjoismaat",
          "type": "number",
          "label": {
              "en": "POP Pohjoismaat?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "maritialStatus": {
          "id": "maritialStatus",
          "type": "list",
          "label": {
              "en": "Elämäntilanne"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question7_valueset1",
          "defaultValue": null
      },
      "popSVakaa": {
          "id": "popSVakaa",
          "type": "number",
          "label": {
              "en": "POP Vakaa?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popS = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group10": {
          "id": "group10",
          "type": "group",
          "label": {
              "en": "Yhteenveto varallisuudesta"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "question4",
              "question6",
              "question7"
          ],
          "className": [
              "hidden-print"
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "popOsuus": {
          "id": "popOsuus",
          "type": "number",
          "label": {
              "en": "POP Osuus?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "group11": {
          "id": "group11",
          "type": "group",
          "label": {
              "en": "Kuva"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
              "question18",
              "attitudeLoss"
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": null
      },
      "children": {
          "id": "children",
          "type": "boolean",
          "label": {
              "en": "Lapsia"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "popBonds": {
          "id": "popBonds",
          "type": "number",
          "label": {
              "en": "Joukkovelkakirjalainat?"
          },
          "description": null,
          "required": null,
          "readOnly": null,
          "items": [
          ],
          "className": [
              "col-1-2"
          ],
          "activeWhen": "popOtherA = true",
          "validations": [
              {
                  "message": {
                      "en": "Tarkista antamasi arvo!"
                  },
                  "rule": "answer < 0"
              }
          ],
          "valueSetId": null,
          "defaultValue": "0"
      },
      "investExperience": {
          "id": "investExperience",
          "type": "list",
          "label": {
              "en": "Asiakkaan yleinen sijoituskokemus?"
          },
          "description": null,
          "required": "true",
          "readOnly": null,
          "items": [
          ],
          "className": [
          ],
          "activeWhen": "",
          "validations": [
          ],
          "valueSetId": "question30_valueset1",
          "defaultValue": null
      }
  },
  "serviceCalls": {
  },
  "metadata": {
      "labels": null,
      "defaultSubmitUrl": null,
      "languages": [
          "en"
      ],
      "label": "Sijoitusneuvonta",
      "created": "2018-01-05T05:18:47.557Z",
      "creator": "c636fae7-9a16-4a43-bd5d-fd79ab5dfe43",
      "tenantId": "pop-dev",
      "lastSaved": "2018-04-02T13:40:24.735Z",
      "savedBy": "c636fae7-9a16-4a43-bd5d-fd79ab5dfe43",
      "valid": true,
      "composer": {
          "contextValues": {
              "var1": "uusi",
              "bankID": "2141234123",
              "hetu": "iooiuuuqwerq"
          }
      },
      "showDisabled": true
  },
  "variables": [
      {
          "name": "totalMonthlyIncome",
          "expression": "normalMonthlyIncome+otherMonthlyIncome+capitalMonthlyIncome",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "totalMonthlyExpens",
          "expression": "normalMonthlyLexpens+normalMonthlyOexpens+otherMonthlyExpenses",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "org",
          "expression": null,
          "defaultValue": "POP Isojoki",
          "context": true,
          "contextType": "text"
      },
      {
          "name": "sub",
          "expression": null,
          "defaultValue": "Tiina Teräs",
          "context": true,
          "contextType": "text"
      },
      {
          "name": "aud",
          "expression": null,
          "defaultValue": "1098127",
          "context": true,
          "contextType": "text"
      },
      {
          "name": "totalFundsPOP",
          "expression": "popFKorkoSalkku+popFVakaa+popFOptimi+popFEurooppa+popFPohjoismaat+popFSuomi+popFMaailma+popFShortInterest+popFLongInterest+popFOtherStock+popFOtherWealth+popFOther",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "netMonthlyIncome",
          "expression": "totalMonthlyIncome - totalMonthlyExpens",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "savings1",
          "expression": "popSKorkoSalkku+popSVakaa+popSOptimi+popSEurooppa+popSPohjoismaat+popSSuomi+popSMaailma+popSOtherWealth",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "savings2",
          "expression": "popSSalkku1+popSSalkku2+popSSalkku3+popSSalkku4+popSSalkku5+popSShortInterest+popSLongInterest+popSOtherStock+popSOther",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "totalSavingsPOP",
          "expression": "savings1 + savings2",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "hetu",
          "expression": null,
          "defaultValue": null,
          "context": true,
          "contextType": "text"
      },
      {
          "name": "otherPOP",
          "expression": "popProperties+popOsuus+popBonds+popPensionSavings+popInsuranceSavings+popDerivatives+popWaranties+popStructuredProducts+popPankAdditionalShares+popStock",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "totalPOP",
          "expression": "totalSavingsPOP+totalFundsPOP+otherPOP+popSavings",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "shortInterest",
          "expression": "popFKorkoSalkku*0.45+popFShortInterest+popSVakaa*0.45+popSSalkku1*0.18+popSSalkku2*0.14\n+popSSalkku3*0.09+popSSalkku4*0.04+popSShortInterest+popOsuus+popSavings",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "cash",
          "expression": "popInsuranceSavings+popSavings",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "longInt1",
          "expression": "popFKorkoSalkku+popFVakaa*0.45+popFOptimi*0.5+popFLongInterest+popSKorkoSalkku+\npopSVakaa*0.45+popSOptimi*0.5+popSSalkku1*0.7+popSSalkku2*0.58",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "longInt2",
          "expression": "popSSalkku3*0.37+popSSalkku4*0.16+popSLongInterest\n+popPankAdditionalShares+popBonds+popPensionSavings+popStructuredProducts",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "shares1",
          "expression": "popFVakaa*0.1+popFOptimi*0.5+popFEurooppa+popFPohjoismaat+popFSuomi+popFMaailma\n+popFOtherStock+popSVakaa*0.1+popSOptimi*0.5+popSEurooppa",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "shares2",
          "expression": "popSPohjoismaat+popSSuomi+popSMaailma+popSSalkku1*0.07+popSSalkku2*0.2\n+popSSalkku3*0.42+popSSalkku4*0.64+popSSalkku5*0.8+popSOtherStock+popStock",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "longInterest",
          "expression": "longInt1+longInt2",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "shares",
          "expression": "shares1+shares2",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "real",
          "expression": "popFOtherWealth+popSOtherWealth+popProperties",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "otherFund",
          "expression": "otherFShortInterest+otherFLongInterest+otherFStock+otherFFund",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "otherSaving",
          "expression": "otherSShortInterest+otherSLongInterest+otherSStock+otherSFund+otherSavings",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "otherWealth",
          "expression": "otherProperties+otherBonds+otherPensionSavings+otherDerivatives+otherWarranties+otherStructuredProducts+otherFWealth+otherSWealth",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "totalOtherAssets",
          "expression": "otherFund+otherSaving+otherWealth",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "alternative",
          "expression": "popFOther+popSSalkku1*0.05+popSSalkku2*0.08+popSSalkku3*0.12+popSSalkku4*0.16\n+popSSalkku5*0.2+popSOther+popDerivatives+popWaranties+popOsuus",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "totalAssests",
          "expression": "totalOtherAssets+totalPOP",
          "defaultValue": null,
          "context": null,
          "contextType": null
      },
      {
          "name": "age",
          "expression": null,
          "defaultValue": "71",
          "context": true,
          "contextType": "number"
      }
  ],
  "valueSets": [
      {
          "id": "question5_valueset1",
          "entries": [
              {
                  "id": "educationA",
                  "label": {
                      "en": "Peruskoulu"
                  }
              },
              {
                  "id": "educationB",
                  "label": {
                      "en": "Toisen asteen koulutus (esim. lukio)"
                  }
              },
              {
                  "id": "educationC",
                  "label": {
                      "en": "Alempi korkeakoulututkinto"
                  }
              },
              {
                  "id": "educationD",
                  "label": {
                      "en": "Ylempi korkeakoulututkinto"
                  }
              },
              {
                  "id": "educationOther",
                  "label": {
                      "en": "Muu"
                  }
              }
          ]
      },
      {
          "id": "question7_valueset1",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "Naimaton"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Avioliitossa/ rekisteröidyssä parisuhteessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Avoliitossa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Leski"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Eronnut"
                  }
              }
          ]
      },
      {
          "id": "question8_valueset1",
          "entries": [
              {
                  "id": "workStatusA",
                  "label": {
                      "en": "Töissä"
                  }
              },
              {
                  "id": "workStatusB",
                  "label": {
                      "en": "Ammatinharjoittaja"
                  }
              },
              {
                  "id": "workStatusC",
                  "label": {
                      "en": "Opiskelija"
                  }
              },
              {
                  "id": "workStatusD",
                  "label": {
                      "en": "Ei työssä/työelämän ulkopuolella"
                  }
              },
              {
                  "id": "workStatusOther",
                  "label": {
                      "en": "Muu"
                  }
              }
          ]
      },
      {
          "id": "question10_valueset1",
          "entries": [
              {
                  "id": "a1",
                  "label": {
                      "en": "Omistusasunto"
                  }
              },
              {
                  "id": "a2",
                  "label": {
                      "en": "Vuokra-asunto"
                  }
              },
              {
                  "id": "a3",
                  "label": {
                      "en": "Asumisoikeusasunto"
                  }
              },
              {
                  "id": "a4",
                  "label": {
                      "en": "Asun vanhempien luonna"
                  }
              },
              {
                  "id": "a5",
                  "label": {
                      "en": "Muu"
                  }
              }
          ]
      },
      {
          "id": "question30_valueset1",
          "entries": [
              {
                  "id": "genInvesEx1",
                  "label": {
                      "en": "Alle vuosi tai ei yhtään"
                  }
              },
              {
                  "id": "genInvesEx2",
                  "label": {
                      "en": "Yksi (1) - kolme (3) vuotta"
                  }
              },
              {
                  "id": "genInvesEx3",
                  "label": {
                      "en": "Kolme (3) vuotta - viisi (5) vuotta"
                  }
              },
              {
                  "id": "genInvesEx4",
                  "label": {
                      "en": "Yli viisi (5) vuotta"
                  }
              }
          ]
      },
      {
          "id": "question31_valueset1",
          "entries": [
              {
                  "id": "investServ1",
                  "label": {
                      "en": "Toimeksiannot ilman sijoitusneuvontaa"
                  }
              },
              {
                  "id": "investServ2",
                  "label": {
                      "en": "Sijoitusneuvontopalvelut"
                  }
              },
              {
                  "id": "investServ3",
                  "label": {
                      "en": "Omaisuudenhoitopalvelut"
                  }
              }
          ]
      },
      {
          "id": "question41_valueset1",
          "entries": [
              {
                  "id": "riskLevel1",
                  "label": {
                      "en": "Säästöjen ja sijoitusten arvo ei saa juuri vaihdella, tuotto-odotus hyvin alhainen."
                  }
              },
              {
                  "id": "riskLevel2",
                  "label": {
                      "en": "Säästöjen ja sijoitusten arvossa voi olla pientä vaihtelua, tuotto-odotus melko matala."
                  }
              },
              {
                  "id": "riskLevel3",
                  "label": {
                      "en": "Säästöjen ja sijoitusten arvossa voi olla kohtuullista vaihtelua. Tuotto-odotus keskimääräinen. "
                  }
              },
              {
                  "id": "riskLevel4",
                  "label": {
                      "en": "Säästöjen ja sijoitusten arvossa voi olla suurta vaihtelua. Tuotto-odotus korkea."
                  }
              }
          ]
      },
      {
          "id": "question42_valueset1",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutan kerran vuodessa"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset11",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question43_valueset1",
          "entries": [
              {
                  "id": "savingsTarget1",
                  "label": {
                      "en": "Asunto / loma-asunto"
                  }
              },
              {
                  "id": "savingsTarget2",
                  "label": {
                      "en": "Auto tai muu kulkuneuvo"
                  }
              },
              {
                  "id": "savingsTarget3",
                  "label": {
                      "en": "Oma eläkeaika"
                  }
              },
              {
                  "id": "savingsTarget4",
                  "label": {
                      "en": "Lapselle säästäminen"
                  }
              },
              {
                  "id": "savingsTarget5",
                  "label": {
                      "en": "Olemassa olevan omaisuuden kartuttaminen"
                  }
              },
              {
                  "id": "savingsTarget6",
                  "label": {
                      "en": "Lainan ohella säästäminen ns. puskuriin"
                  }
              },
              {
                  "id": "savingsTarget7",
                  "label": {
                      "en": "Lomamatka"
                  }
              },
              {
                  "id": "SavingsTarget",
                  "label": {
                      "en": "Muu iso hankinta"
                  }
              }
          ]
      },
      {
          "id": "question44_valueset1",
          "entries": [
              {
                  "id": "attitudeLevel1",
                  "label": {
                      "en": "Myisin kaiken"
                  }
              },
              {
                  "id": "attitudeLevel2",
                  "label": {
                      "en": "Myisin osan sijoituksistani"
                  }
              },
              {
                  "id": "attitudeLevel3",
                  "label": {
                      "en": "En tekisi mitään"
                  }
              },
              {
                  "id": "attitudeLevel4",
                  "label": {
                      "en": "Ostaisin lisää samaa sijoitustuotetta"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset111",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset112",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset1121",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset11211",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset112111",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question42_valueset1121111",
          "entries": [
              {
                  "id": "e1",
                  "label": {
                      "en": "En koskaan"
                  }
              },
              {
                  "id": "e2",
                  "label": {
                      "en": "Harvemmin kuin kerran vuodessa"
                  }
              },
              {
                  "id": "e3",
                  "label": {
                      "en": "Muutaman kerran vuodessa"
                  }
              },
              {
                  "id": "e4",
                  "label": {
                      "en": "Kuukausittain"
                  }
              },
              {
                  "id": "e5",
                  "label": {
                      "en": "Viikoittain"
                  }
              }
          ]
      },
      {
          "id": "question5_valueset2",
          "entries": [
              {
                  "id": "k1",
                  "label": {
                      "en": "Anna suositus"
                  }
              }
          ]
      },
      {
          "id": "question7_valueset2",
          "entries": [
              {
                  "id": "k1",
                  "label": {
                      "en": "POP Suomi"
                  }
              },
              {
                  "id": "k2",
                  "label": {
                      "en": "POP Eurooppa"
                  }
              }
          ]
      },
      {
          "id": "question7_valueset3",
          "entries": [
              {
                  "id": "s1",
                  "label": {
                      "en": "POP Suomi"
                  }
              },
              {
                  "id": "s2",
                  "label": {
                      "en": "POP Eurooppa"
                  }
              },
              {
                  "id": "s3",
                  "label": {
                      "en": "POP Maailma"
                  }
              }
          ]
      },
      {
          "id": "question9_valueset1",
          "entries": [
              {
                  "id": "k1",
                  "label": {
                      "en": "Käteinen"
                  }
              },
              {
                  "id": "k2",
                  "label": {
                      "en": "Lyhytkorko"
                  }
              },
              {
                  "id": "k3",
                  "label": {
                      "en": "Pitkäkorko"
                  }
              },
              {
                  "id": "k4",
                  "label": {
                      "en": "Osake"
                  }
              },
              {
                  "id": "k5",
                  "label": {
                      "en": "Reaaliomasuus"
                  }
              }
          ]
      },
      {
          "id": "clientInvestPeriod_valueset1",
          "entries": [
              {
                  "id": "invPeriod1",
                  "label": {
                      "en": "1 - 2 vuotta"
                  }
              },
              {
                  "id": "invPeriod2",
                  "label": {
                      "en": "2 - 3 vuotta"
                  }
              },
              {
                  "id": "invPeriod3",
                  "label": {
                      "en": "3 - 5 vuotta"
                  }
              },
              {
                  "id": "invPeriod4",
                  "label": {
                      "en": "5 - 10 vuotta"
                  }
              },
              {
                  "id": "invPeriod5",
                  "label": {
                      "en": "Yli 10 vuotta"
                  }
              }
          ]
      },
      {
          "id": "clientInvestPeriod_valueset2",
          "entries": [
              {
                  "id": "invPeriod1",
                  "label": {
                      "en": "1 - 2 vuotta"
                  }
              },
              {
                  "id": "invPeriod2",
                  "label": {
                      "en": "2 - 3 vuotta"
                  }
              },
              {
                  "id": "invPeriod3",
                  "label": {
                      "en": "3 - 5 vuotta"
                  }
              },
              {
                  "id": "invPeriod4",
                  "label": {
                      "en": "5 - 10 vuotta"
                  }
              },
              {
                  "id": "invPeriod5",
                  "label": {
                      "en": "Yli 10 vuotta"
                  }
              }
          ]
      }
  ]
}

export default DEBUG_FORM;
