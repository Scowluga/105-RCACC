var FORM_ID = ""; 
var SHEET_ID = "";

var RANK_ID = ""; 
var LAST_ID = ""; 
var FIRST_ID = ""; 
var EMAIL_ID = ""; 
var INSPECT_ID = ""; 


// --------------------- UTILITY FUNCTIONS ------------------------------- 

function findId() { // FOR TESTING PURPOSES: getting ID of dropdown 
  var name = "Your Email Address"; // name of field you want ID for 
  
  var form = FormApp.openById(FORM_ID); 
  var items = form.getItems();
  items.map(function(item, index) {
    Logger.log(item.getTitle()); 
    if (item.getTitle() == name) {
      Logger.log("Item with name " + name + " found with id " + item.getId()); 
      return item; 
    }
  });
}

function debugEvent(event) { // gets a string to debug event form submission 
  // SET DEBUGEVENT AS ON SUBMIT TRIGGER 
  MailApp.sendEmail("david.scowluga@gmail.com", "event string", event.namedValues["Inspect!"].toString()); 
  Logger.log("Debug Event String: " + event.namedValues["Inspect!"].toString());
}


// -------------------- SETUP FUNCTIONS -----------------------

function getDate() { // Getting date yyyy-MM-dd 
  return new Date().toISOString().slice(0,10); 
}


function getCadetRow(data, rank, last, first) { // getting row of cadet with details 
  for (i in data) {
    if (data[i][0] == rank && data[i][1] == last && data[i][2] == first) { // last name and first name equal 
        return parseInt(i); 
      };  
  }; 
  return -1; // cadet with first and last name star level not found 
}; 

function setUpLast(data, row) { // getting last inspection results from data 
  var temp = []; 
  for (i = 15; i < 23; i ++) { // column 16-24 is the previous inspection 
    var tempo = data[row][i]; 
    temp.push(tempo); 
  }
  return temp; 
}

function setUpThis(event) { // getting inspection results from event 
  var name; 
  var temp = []; 
  var names = ["Inspect! [Beret]", "Inspect! [Hair]", "Inspect! [Tie]", "Inspect! [Tunic (Ironed) ]", "Inspect! [Tunic (Badges) ]", "Inspect! [Tunic (Other)]", "Inspect! [Pants]", "Inspect! [Boots]"];
  for (i = 0; i < names.length; i++) {
    name = names[i]; 
    temp.push(event.namedValues[name].toString()); // -3 to +3 (string) 
  }
  return temp; 
}


// ------------------------------- TRIGGERS -----------------------------------------


function onOpen() { // NOW ONCHANGE, UPDATES DROPDOWNS 
  var sheet = SpreadsheetApp.openById(SHEET_ID); 
  var ss = sheet.getSheetByName("Data");
  var form = FormApp.openById(FORM_ID); 
  
  var rankDrop = form.getItemById(RANK_ID).asListItem(); 
  var lastDrop = form.getItemById(LAST_ID).asListItem(); 
  var firstDrop = form.getItemById(FIRST_ID).asListItem(); 
  var emailDrop = form.getItemById(EMAIL_ID).asListItem(); 
  
  var rankList = []; 
  var lastList = []; 
  var firstList = []; 
  
  var data = ss.getDataRange().getValues(); 
  
  for (i = 3; i < data.length; i ++) {
    if (i > 2) { // title + example rows 
      if (rankList.indexOf(data[i][0]) < 0 && data[i][0].trim().length > 1) { // doesn't contain
        rankList.push(data[i][0]); 
      }; 
      if (lastList.indexOf(data[i][1]) < 0 && data[i][1].trim().length > 1) {
        lastList.push(data[i][1]); 
      }; 
      if (firstList.indexOf(data[i][2]) < 0 && data[i][2].trim().length > 1) {
        firstList.push(data[i][2]); 
      }; 
    }; 
  }
  
  rankList.sort(); 
  rankList = ["Green", "Red", "Silver", "Gold", "Master", "Master (Qualified)"]; // CHANGED FROM LAST NAME TO RANK 
  
  lastList.sort(); 
  firstList.sort(); 

  rankDrop.setChoiceValues(rankList); // setting values to lists 
  lastDrop.setChoiceValues(lastList); 
  firstDrop.setChoiceValues(firstList); 
  
  // EMAILS 
  var emailList = []; 
  
  var mails = sheet.getSheetByName("Emails");
  var info = mails.getDataRange().getValues(); 
  for (i in info) {
    emailList.push(info[i][0]); 
  }
  emailDrop.setChoiceValues(emailList); 
    
}

function updateTotals(thisInspection, data, row) { // UPDATES THE TOTAL INCREMENTING COUNTS 
  
  var general = SpreadsheetApp.openById(SHEET_ID).getSheetByName("Info").getDataRange().getValues(); 
  var weightList = [];
  
  for (i = 1; i < 9; i ++) {
    weightList.push(general[i][2]); 
  }
  
  
  var temp; // temp value 
  var score = 0; // total score 
  
  for (i = 3; i < 11; i ++) {
    temp = parseInt(data[row][i]) + parseInt(thisInspection[i - 3]); // total after increment 
    score += temp * parseFloat(weightList[i - 3]); // increment score 
    data[row][i] = temp; // set value 
  }; 
  
  data[row][11] = parseInt(data[row][11]) + 1; // increment total 
  
  return parseInt(score) / parseInt(data[row][11]); 
}; 

function resetLastInspection(thisInspection, data, row) { // RESETS THE LAST INSPECTION TO THE INSPECTION THAT JUST HAPPENED 
  
  for (i = 15; i < 23; i ++) {
    data[row][i] = parseInt(thisInspection[i - 15]); // set the spot as the new value 
  }
  var date = data[row][14]; // finding date of last 
  data[row][14] = getDate(); // updating date 
  return date; 
}; 

function getSpacedString(number) {
  var ret = number.toString(); 
  if (number.toString().length == 1) {
    ret = " " + number.toString(); 
  }
  return ret; 
}; 

function setUpEmail(date, score, lastInspection, thisInspection) { // RETURNS STRING FRON INFORMATION 

  var general = SpreadsheetApp.openById(SHEET_ID).getSheetByName("Info").getDataRange().getValues(); 
  
  var namesList = [];
  var weightList = []; // not used, but here in case I need it 
  
  for (i = 1; i < 9; i ++) {
    namesList.push(general[i][0]); 
    weightList.push(general[i][2]); 
  }

  var message = "<p style='font-family: monospace; white-space: pre; font-size: 14px;'>"; 
  message += "The last inspection for this cadet was on: " + date + "." + "\n\n"; 
  message += "                  last current" + "\n"; 
  
  var temp; 
  for (i = 0; i < 8; i ++) {
    temp = namesList[i] + "      " + getSpacedString(lastInspection[i]); 
    temp += "      " + getSpacedString(thisInspection[i]); 
    message += temp + "\n"; 
  }; 
  
  message += "\n" + "Remember, positive and negative 3 ratings should be very rare, and reoccurring ones dictate either a blue chit (+3) or red chit (-3) to be given to the cadet."; 
  message += "\n" + "Their total score is: " + (Math.round(score * 100) / 100).toString(); 
  message += "</p>"; 
  return message; 
}; 


function onSubmit(event) { // ONSUBMIT LOGIC  

  // Note that rank has been replaced by Star Level due to querying purposes 

  // DEBUG RANDOM EVENT 
  // var event = {"values":["5/11/2017 14:01:01","Sgt.","Lu","David","1","1","1","1","1","1","1","1","david.scowluga@gmail.com"],"namedValues":{"Inspect! [Hair]":["1"],"Your Email Address":["david.scowluga@gmail.com"],"Cadet's Last Name":["Lu"],"Inspect! [Boots]":["1"],"Inspect! [Tie]":["1"],"Inspect! [Pants]":["1"],"Inspect! [Tunic (Ironed) ]":["1"],"Timestamp":["5/11/2017 14:01:01"],"Inspect! [Tunic (Other)]":["1"],"Inspect! [Beret]":["1"],"Cadet's Rank":["Sgt."],"Cadet's First Name":["David"],"Inspect! [Tunic (Badges) ]":["1"]},"range":{"columnStart":1,"rowStart":16,"rowEnd":16,"columnEnd":13},"source":{},"authMode":{},"triggerUid":402497048}; 

  var ss = SpreadsheetApp.openById(SHEET_ID).getSheetByName("Data");

  // Finding three inputted queries for cadet 
  var rank = event.namedValues["Cadet's Star Level"].toString(); 
  var last = event.namedValues["Cadet's Last Name"].toString(); 
  var first = event.namedValues["Cadet's First Name"].toString(); 
  
  var data = ss.getDataRange().getValues(); // String[][] 
  var row = getCadetRow(data, rank, last, first); // cadets row. returns -1 if not found (first + last name) 
  
  // setting up email 
  var recipient = event.namedValues["Your Email Address"].toString(); 
  var subject = "Inspection for " + rank + " " + first + " " + last; 
  
  if (row == -1) { // cadet not found 
    MailApp.sendEmail(recipient, subject, "Error, your cadet could not be found in the spreadsheet. Please make sure you have inputted the correct star level, first, and last names.");
    return; 
  }
  
  // Else, continue 
  var lastInspection = setUpLast(data, row); // find values of last inspection ie [-3, -2, -1...] 
  var thisInspection = setUpThis(event); // find current inspection values
  
  var score = updateTotals(thisInspection, data, row); // UPDATE TOTAL VALUES + RETURNS SCORE 
  var date = resetLastInspection(thisInspection, data, row); // UPDATE LAST INSPECTION + RETURNS DATE
  
  // updating score 
  data[row][12] = score; 
  
  var body = setUpEmail(date, score, lastInspection, thisInspection); // create HTML body for email with table of results 
  
  ss.getDataRange().setValues(data); // SET THE NEW DATA TO SPREADSHEET 
  MailApp.sendEmail(recipient, subject, "", {htmlBody : body}); // send email 
  
  onOpen(); // just to reset up 
}


