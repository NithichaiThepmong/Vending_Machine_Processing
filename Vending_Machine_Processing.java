/////////////////////////////////////////////////////////////Global Variable
final int sizeProducts = 5, sizeCoins = 3;               //Capacity of product's number and type of coin
boolean[] canChooseProducts = new boolean[sizeProducts]; //Can you choose this products?
boolean[] isProductsOut = new boolean[sizeProducts];     //Is product is out? It's can't choose.
int[] numberProducts = new int[sizeProducts];            //Number of product in machine
int[] priceProducts = new int[sizeProducts];             //Price to buy products 
int[] soldProducts = new int[sizeProducts];              //Number of machine sold its
int[] numberCoinMonitor = new int[sizeCoins];            //Number of coin when you put it
int[] numberCoinMachine = new int[sizeCoins];            //Number of coin in machine
int[] numberCoinChange = new int[sizeCoins];             //Output coin for change
/////////////////////////////////////////////////////////////Slide Bar
//X of slide bar -> value of datas
int[] xSlideProducts = new int[sizeProducts];
int[] xSlideCoins = new int[sizeCoins];
int[] xSlidePrices = new int[sizeProducts];
////////////////////////////////////////////////////////////Do you slide bar?
boolean[] isSlideProducts = new boolean[sizeProducts];
boolean[] isSlideCoins = new boolean[sizeCoins];
boolean[] isSlidePrices = new boolean[sizeProducts];
/////////////////////////////////////////////////////////////Admin Text
int sizeAdminText = 4;                                   //Capacity of history text
String[] adminTexts = new String[sizeAdminText];         //Data of history text
/////////////////////////////////////////////////////////////Other
String mode = "MACHINE";                                 //Mode MACHINE and ADMIN
int xOffset;                                             //X of background
String typeSelected;                                     //What products do you select?
int monitorMoney;
int change;
int soldBalance;
////////////////////////////////////////////////////////////Setup Part
void setup() {
  size(700, 550);
  frameRate(60);
  resetSimulate();                 //Default Value Setting
}
////////////////////////////////////////////////////////////Update & Draw Part
void draw() {
  ///////////////////////////////////////////Update Value
  if (mode == "MACHINE") {
    int dx = 25;                    //Slide to Machine Monitor(slide to right)
    xOffset += dx;
    if (xOffset > 0) {
      xOffset = 0;                  //Limit at 0
    }
  } else if (mode == "ADMIN") {
    int dx = 25;                    //Slide to Admin Monitor (Slide to left)
    xOffset -= dx;
    if (xOffset < -width) {
      xOffset = -width;             //Limit at -width
    }
  }
  int xMinLeft = 100, xMaxLeft = 300;
  int minValue = 0, maxValue = 100;
  int xMinRight = 450, xMaxRight = 650;
  checkAvailableChoose(xMinLeft, xMaxLeft, minValue, maxValue);      //Can I choose product.
  ///////////////////////////////////////////////Check X position of slides(Admin mode)
  checkXSlide(numberProducts, xSlideProducts, xMinLeft, xMaxLeft, minValue, maxValue);
  checkXSlide(numberCoinMachine, xSlideCoins, xMinLeft, xMaxLeft, minValue, maxValue);
  checkXSlide(priceProducts, xSlidePrices, xMinRight, xMaxRight, minValue, maxValue);
  /////////////////////////////////////////////////////Draw
  background(#FFCE4B);
  drawModeTab();
  drawMachine(xOffset);
  drawAdmin(xOffset + width, xMinLeft, xMaxLeft, xMinRight, xMaxRight);
}
/////////////////////////////////Machine Mode
//Selected Product
void selectedProduct(String type, int xMinLeft, int xMaxLeft) {
  int minValue= 0, maxValue = 100;
  typeSelected = type;
  //Check can you buy?
  canMachineGetCoin();
  calculateCoinChange(type, xMinLeft, xMaxLeft, minValue, maxValue);
  //Give change ?
  if (typeSelected != "CANNOTBUY") {
    addCoinMachine(xMinLeft, xMaxLeft, minValue, maxValue);
    increaseSoldValue(type);
    decreaseProduct(type, xMinLeft, xMaxLeft, minValue, maxValue);
    setCoinChange(xMinLeft, xMaxLeft, minValue, maxValue);
    setHistoryText("YOU SOLD " + type);
  } else {
    change = monitorMoney;
    for (int i = 0; i < numberCoinMonitor.length; i++) {
      numberCoinChange[i] = numberCoinMonitor[i];
    }
  }
  allChooseFailed();
  resetMonitorMoney();
}
////////////////////////////Can machine get coins?
void canMachineGetCoin() {
  for (int i = 0; i < numberCoinMachine.length; i++) {
    if (numberCoinMachine[i] + numberCoinMonitor[i] > 100) {
      println(numberCoinMachine[i], numberCoinMonitor[i]);
      typeSelected = "CANNOTBUY";
    }
  }
}
////////////////////////Decrease product
void decreaseProduct(String type, int xMinLeft, int xMaxLeft, int minValue, int maxValue) {
  String[] types = {"EGG", "BOILED", "FRIED", "OMELET", "STUFFED"};
  for (int i = 0; i < types.length; i++) {
    if (type == types[i]) {
      //Decrease X of products slides
      xSlideProducts[i] -= (xMaxLeft - xMinLeft) / (maxValue - minValue);
    }
  }
  checkXSlide(numberProducts, xSlideProducts, xMinLeft, xMaxLeft, minValue, maxValue);
}
/////////////////////////////Add coin in machine
void addCoinMachine(int xMin, int xMax, int minValue, int maxValue) {
  for (int index = 0; index < numberCoinMonitor.length; index++) {
    //Add 
    xSlideCoins[index] += (xMax - xMin) / (maxValue - minValue) * numberCoinMonitor[index];
  }
  checkXSlide(numberProducts, xSlideProducts, xMin, xMax, minValue, maxValue);
}
////////////////////////////Calculation for give change
void calculateCoinChange(String type, int xMin, int xMax, int minValue, int maxValue) {
  String[] types = {"EGG", "BOILED", "FRIED", "OMELET", "STUFFED"};
  for (int index = 0; index < types.length; index++) {
    if (type == types[index]) {
      change = monitorMoney - priceProducts[index];
    }
  }
  int one = 0, five = 1, ten = 2;
  if (numberCoinMachine[ten] >= change / 10) {
    numberCoinChange[ten] = change / 10;
    if (numberCoinMachine[five] >= change % 10 / 5) {
      numberCoinChange[five] = change % 10 / 5;
      if (numberCoinMachine[one] >= change % 10 % 5) {
        numberCoinChange[one] = change % 10 % 5;
      } else {
        typeSelected = "CANNOTBUY";
      }
    } else if (numberCoinMachine[one] > change % 10) {
      numberCoinChange[one] = change % 10;
    } else {
      typeSelected = "CANNOTBUY";
    }
  } else if (numberCoinMachine[five] > change / 5) {
    numberCoinChange[five] = change / 5;
    if (numberCoinMachine[one] > change % 5) {
      numberCoinChange[one] = change % 5;
    } else {
      typeSelected = "CANNOTBUY";
    }
  } else if (numberCoinMachine[one] > change) {
    numberCoinChange[one] = change;
  } else {
    typeSelected = "CANNOTBUY";
  }
}
/////////////////////////Set Coin in machine
void setCoinChange(int xMin, int xMax, int minValue, int maxValue) {
  for (int index = 0; index < numberCoinChange.length; index++) {
    xSlideCoins[index] -= numberCoinChange[index] * ((xMax - xMin) / (maxValue - minValue));
  }
  checkXSlide(numberCoinMachine, xSlideCoins, xMin, xMax, minValue, maxValue);
}
/////////////////////////Increase number of sold products
void increaseSoldValue(String type) {
  String[] types = {"EGG", "BOILED", "FRIED", "OMELET", "STUFFED"};
  for (int index = 0; index < types.length; index++) {
    if (type == types[index]) {
      soldProducts[index]++;
      soldBalance += soldProducts[index] * priceProducts[index];
    }
  }
}
///////////////////////////Increase Money Monitor
void increaseCoinMonitor(String type) {
  int one = 0, five = 1, ten = 2;
  if (type == "ONE") {
    numberCoinMonitor[one]++;
    monitorMoney++;
  } else if (type == "FIVE") {
    numberCoinMonitor[five]++;
    monitorMoney += 5;
  } else if (type == "TEN") {
    numberCoinMonitor[ten]++;
    monitorMoney += 10;
  }
  setHistoryText("YOU RECEIVE " + type + " COIN");
}
//////////////////Check if you click cancel
void cancelMouse(int xCancel, int yCancel, int widthCancel, int heightCancel) {
  int xMin = 100, xMax = 300;
  int minValue = 0;
  if (isClick(xCancel, yCancel, widthCancel, heightCancel)) {
    cancel(xMin, xMax, minValue, minValue);
  }
}
///////////////////////Cancel
void cancel(int xMin, int xMax, int minValue, int maxValue) {
  typeSelected = "CANCEL";
  setHistoryText("YOU PRESSED CANCEL");
  change = monitorMoney;
  for (int i = 0; i < numberCoinMonitor.length; i++) {
    numberCoinChange[i] = numberCoinMonitor[i];
  }
  allChooseFailed();
  resetMonitorMoney();
}
///////////////////////Reset machine
void resetMachine() {
  allChooseFailed();
  resetMonitorMoney();
  //resetHistory();
  typeSelected = null;
}
//////////////////////Reset Simulation
void resetSimulate() {
  allChooseFailed();
  resetNumberProduct();
  resetMonitorMoney();
  resetCoinMachine();
  resetPrices();
  resetHistory();
  resetSoldProducts();
  soldBalance = 0;
  typeSelected = null;
}
///////////////////////////All number product set to default
void resetNumberProduct() {
  int[] xDatas = {200, 200, 160, 160, 140};  //Default of slide bar
  for (int index = 0; index < xSlideProducts.length; index++) {
    xSlideProducts[index] = xDatas[index];   //x of product bar move to default
  }
}
//////////////////////////All coin in machine set to default
void resetCoinMachine() {
  int[] xCoins = {250, 250, 250};
  for (int index = 0; index < xSlideCoins.length; index++) {
    xSlideCoins[index] = xCoins[index];      //x of coin bar move to default
  }
}
///////////////////////////All price of product set to default
void resetPrices() {
  //550, 650
  int[] xPrices = {460, 470, 476, 484, 516};
  for (int index = 0; index < xSlidePrices.length; index++) {
    xSlidePrices[index] = xPrices[index];     //x of price bar move to default
  }
}
///////////////////////////Clear history texts
void resetHistory() {
  for (int index = 0; index < adminTexts.length; index++) {
    adminTexts[index] = null;
  }
}
//////////////////////////Set to cannot choose all products
void allChooseFailed() {
  for (int index = 0; index < canChooseProducts.length; index++) {
    canChooseProducts[index] = false;
  }
}
///////////////////////////Set input money to zero
void resetMonitorMoney() {
  for (int index = 0; index < numberCoinMonitor.length; index++) {
    numberCoinMonitor[index] = 0;
  }
  monitorMoney = 0;
}
////////////////////////////Set products are sold and balance of sold to zero
void resetSoldProducts() {
  for (int i = 0; i < soldProducts.length; i++) {
    soldProducts[i] = 0;
  }
  soldBalance = 0;
}
///////////////////////////////////Remaining Products to sold it
void checkNumberProduct(int xMin, int xMax, int minValue, int maxValue) {
  for (int index = 0; index < numberProducts.length; index++) {
    if (numberProducts[index] <= 0) {
      isProductsOut[index] = true;
    } else {
      isProductsOut[index] = false;
    }
  }
}
///////////////////////////////////Coin Remaining to give change
void checkCoinMachine(int xMin) {
  int one = 0, five = 1, ten = 2;
  if (xSlideCoins[one] < xMin) {
    xSlideCoins[one] = xMin;
  }
  if (xSlideCoins[five] < xMin) {
    xSlideCoins[five] = xMin;
  }
  if (xSlideCoins[ten] < xMin) {
    xSlideCoins[ten] = xMin;
  }
}
////////////////////////////////////Can you buy product?
void checkAvailableChoose(int xMin, int xMax, int minValue, int maxValue) {
  checkNumberProduct(xMin, xMax, minValue, maxValue);
  for (int index = 0; index < canChooseProducts.length; index++) {
    if (monitorMoney >= priceProducts[index] && !isProductsOut[index]) {
      canChooseProducts[index] = true;
    } else {
      canChooseProducts[index] = false;
    }
  }
}
///////////////////////////////////////////////Admin Mode
/////////////////////////////////Set History text
void setHistoryText(String text) {
  for (int index = adminTexts.length - 1; index > 0; index--) {
    adminTexts[index] = adminTexts[index - 1];
  }
  adminTexts[0] = setTime() + text;
}
//////////////////////Time Update
String setTime() {
  String time = nf(hour(), 2) + " : " + nf(minute(), 2) + " : " + nf(second(), 2) + " | ";
  return time;
}
///////////////////////////////////Change x Slide to value
void checkXSlide(int[] numberDatas, int[] xDatas, int xMin, int xMax, int valueMin, int valueMax) {
  for (int i = 0; i < xDatas.length; i++) {
    if (xDatas[i] < xMin) {
      xDatas[i] = xMin;
    } else if (xDatas[i] > xMax) {
      xDatas[i] = xMax;
    }
    numberDatas[i] = int(map(xDatas[i], xMin, xMax, valueMin, valueMax));
  }
}
/////////////////////////////////////////////////Check when click
///////////////////////////////Is click on?
boolean isClick(int xStart, int yStart, int widthArea, int heightArea) {
  return mouseX > xStart && mouseX < xStart + widthArea && mouseY > yStart && mouseY < yStart + heightArea;
}
/////////////////////////////////////Is click on (Circle)?
boolean isClickRadius(int xOffset, int yOffset, int radius) {
  return radius > dist(mouseX, mouseY, xOffset, yOffset);
}
//////////////////////////////////////////Is click on product selected?
void productSelectedMouse(int xProducts[], int yProducts[]) {
  int sizeProduct = 100, xMinLeft = 100, xMaxLeft = 300;
  String[] typeProduct = {"EGG", "BOILED", "FRIED", "OMELET", "STUFFED"};
  for (int index = 0; index < typeProduct.length; index++) {
    if (isClick(xProducts[index], yProducts[index], sizeProduct, sizeProduct) && canChooseProducts[index]) {
      selectedProduct(typeProduct[index], xMinLeft, xMaxLeft);
    }
  }
}
////////////////////////////////////////////////Is click to increase coin
void coinMonitorNumberMouse(int xCoinMonitors, int yCoinMonitors[]) {
  int widthLabel = 25, heightLabel = 30;
  String[] type = {"ONE", "FIVE", "TEN"};
  for (int index = 0; index < yCoinMonitors.length; index++) {
    if (isClick(xCoinMonitors, yCoinMonitors[index] - heightLabel / 2, widthLabel, heightLabel)) {
      increaseCoinMonitor(type[index]);
    }
  }
}
/////////////////////////////////////////////////////////////Draw
///////////////////////Draw Mode Tab
void drawModeTab() {
  //Drwa Selected Tab
  int xTab = 0, yTab = 0, widthTab = width / 2, heightTab = 50;
  noStroke();
  fill(#F39C11);
  rect(xTab, yTab, widthTab, heightTab);
  rect(xTab + widthTab, yTab, widthTab, heightTab);
  /////////////////////////////Draw Machine Symbol
  fill(#FFCE4B);
  ellipse(xTab + widthTab/2, yTab + heightTab/2, widthTab * 0.075, heightTab * 0.7);
  /////////////////////////////////Draw Admin Symbol
  ellipse(xTab + widthTab * 1.5, yTab + heightTab * 0.3, widthTab * 0.05, heightTab * 0.4);
  arc(xTab + widthTab * 1.5, yTab + heightTab * 0.8, widthTab * 0.07, heightTab * 0.4, -PI, 0);
  /////////////////////////////Draw line divide tab
  stroke(#FFCE4B);
  strokeWeight(5);
  line(xTab + widthTab, yTab, xTab + widthTab, yTab + heightTab);
}
//////////////////////////////////////////////////Machine Mode
//////////////////////////////Draw machine mode set
void drawMachine(int xOffset) {
  drawProducts(xOffset);                  //Draw product selection
  drawTerminal(xOffset, typeSelected);    //Draw terminal text area
  drawRightTab(xOffset);                  //Draw right tab menu
}
/////////////////////////////////Draw products button
void drawProducts(int xOffset) {
  int xProducts[] = {50, 200, 350, 125, 275};
  int yProducts[] = {75, 75, 75, 250, 250};
  String[] typeProduct = {"EGG", "BOILED", "FRIED", "OMELET", "STUFFED"};
  for (int index = 0; index < priceProducts.length; index++) {
    drawChooseMenu(xOffset + xProducts[index], yProducts[index], priceProducts[index]
      , numberProducts[index], typeProduct[index], canChooseProducts[index]);
  }
}
//////////////////////////////Draw choose 
void drawChooseMenu(int xAd, int yAd, int price, int numEgg, String type, boolean isChoose) {
  int widthAd = 100, heightAd = 150;
  noStroke();
  ///////////////////////Draw BG color
  if (isChoose) {
    fill(#008D31);
  } else {
    fill(#C82C1D);
  }
  rect(xAd, yAd, widthAd, 0.6 * heightAd);
  /////////////////////Draw Black TextBox
  fill(0);
  rect(xAd, yAd + 0.6 * heightAd, widthAd, 0.4 * heightAd);
  ///////////////////////Draw Product image
  if (type == "EGG") {
    drawEgg(xAd + widthAd/2, yAd + heightAd/3, 0.5 * widthAd, 0.5 * heightAd);
  } else if (type == "BOILED") {
    drawBoiledEgg(xAd + widthAd/2, yAd + heightAd/3, 0.5 * widthAd, 0.5 * heightAd);
  } else if (type == "FRIED") {
    drawFriedEgg(xAd + widthAd/2, yAd + heightAd/3, 0.7 * widthAd, 0.3 * heightAd);
  } else if (type == "OMELET") {
    drawOmelet(xAd + widthAd/2, yAd + heightAd/3, 0.7 * widthAd, 0.3 * heightAd);
  } else if (type == "STUFFED") {
    drawStuffed(xAd + widthAd/2, yAd + heightAd/3, 0.7 * widthAd, 0.3 * heightAd);
  }
  ////////////////////////Draw Description
  drawTextProduct(xAd, yAd, widthAd, heightAd, type, price, numEgg);
}
/////////////////////////////////Draw Description(name, number, price)
void drawTextProduct(float xAd, float yAd, float widthAd, float heightAd, String type, int price, int numEgg) {
  fill(255);
  textAlign(CENTER, CENTER);
  text(type, xAd + widthAd/2, yAd + 0.7 * heightAd);
  text(numEgg, xAd + widthAd/2, yAd + 0.9 * heightAd);
  text(price + " Baht", xAd + widthAd/2, yAd + 0.8 * heightAd);
}
//////////////////////////Draw normal egg
void drawEgg(float xPic, float yPic, float widthPic, float heightPic) {
  //Shadow
  fill(0, 50);
  ellipse(xPic + 5, yPic + 5, widthPic, heightPic);
  //Egg
  fill(#FDE3A8);
  ellipse(xPic, yPic, widthPic, heightPic);
}
//////////////////////////////////Draw boiled egg
void drawBoiledEgg(float xPic, float yPic, float widthPic, float heightPic) {
  //Shadow
  fill(0, 50);
  ellipse(xPic + 5, yPic + 5, widthPic, heightPic);
  //Egg
  fill(#FFFFFF);
  ellipse(xPic, yPic, widthPic, heightPic);
}
/////////////////////////////////Draw fried egg
void drawFriedEgg(float xPic, float yPic, float widthPic, float heightPic) {
  //Shadow
  fill(0, 50);
  ellipse(xPic + 5, yPic + 5, widthPic, heightPic);
  //Egg
  fill(#FFFFFF);      //White
  ellipse(xPic, yPic, widthPic, heightPic);
  fill(#F9B32F);      //Yellow
  ellipse(xPic, yPic, widthPic * 0.4, heightPic * 0.4);
}
////////////////////////////////Draw omelet
void drawOmelet(float xPic, float yPic, float widthPic, float heightPic) {
  //Shadow
  fill(0, 50);
  ellipse(xPic + 5, yPic + 5, widthPic, heightPic);
  //Egg
  fill(#F9B32F);
  ellipse(xPic, yPic, widthPic, heightPic);
}
////////////////////////////////Draw stuffed omelet
void drawStuffed(float xPic, float yPic, float widthPic, float heightPic) {
  //Shadow
  fill(0, 50);
  ellipse(xPic + 5, yPic + 5, widthPic, heightPic);
  //Egg
  fill(#F9B32F);
  ellipse(xPic, yPic, widthPic, heightPic);
  //Sauce
  noFill();
  stroke(#E84C3D);
  strokeWeight(3);
  strokeCap(ROUND);
  for (int i = -3; i < 3; i++) {
    if (i % 2 == 0) {
      //Go to northeast
      line(xPic + i * widthPic/10, yPic - heightPic / 10, xPic + (i+1) * widthPic/10, yPic + heightPic / 10);
    } else {
      //Go to southwest
      line(xPic + i * widthPic/10, yPic + heightPic / 10, xPic + (i+1) * widthPic/10, yPic - heightPic / 10);
    }
  }
}
//////////////////////////////////////////////Draw Terminal text box
void drawTerminal(int xOffset, String type) {  
  int widthBox = width, heightBox = 110, yBox = height - heightBox;
  String text = null;
  fill(255);
  //Selected text
  if (type == "EGG") {
    text = "You get eggs";
  } else if (type == "BOILED") {
    text = "You get boiled eggs";
  } else if (type == "FRIED") {
    text = "You get fried eggs";
  } else if (type == "OMELET") {
    text = "You get omelet";
  } else if (type == "STUFFED") {
    text = "You get stuffed omelet";
  } else if (type == "CANCEL") {
    text = "Cancel";
  } else if (type == "CANNOTBUY") {
    text = "You can't buy product";
  }
  //Draw Text box
  noStroke();
  if (typeSelected != null) {  //Do you have text?
    fill(#B85400);
    rect(xOffset, yBox, widthBox, heightBox);
    fill(255);
    textAlign(LEFT, TOP);
    //Draw What do you select?
    text(text, xOffset + 10, yBox + 10);
    //Draw change and number coin for change
    drawChangeText(xOffset, yBox, heightBox);
  } else {
    fill(#B85400);
    rect(xOffset, yBox, widthBox, heightBox);
  }
}
////////////////////////////////Draw change and number coin for change
void drawChangeText(int xOffset, int yBox, int heightBox) {
  int space = 20;
  String coinsName[] = {"One Baht", "Five Baht", "Ten Baht"};
  int numberChange = 0;
  for (int index = 0; index < numberCoinChange.length; index++) {
    numberChange += numberCoinChange[index];
  }
  textAlign(LEFT, TOP);
  text("Change : " + change + " Bath | Number of change : " + numberChange, xOffset + 10, yBox + 30);
  for (int index = 0; index < numberCoinChange.length; index++) {
    text(coinsName[index] + " : " + numberCoinChange[index] + " Coins", xOffset + 10, yBox  + index * space  + 50);
  }
}
////////////////////////////////////Draw Right Tab
void drawRightTab(int xOffset) {
  int xPriceMonitor = 525, yPriceMonitor = 75;
  int xCancelButton = 550, yCancelButton = 325;
  int xMoneyLabel = 550;
  int[] yMoneyLabel = {175, 225, 275};
  int[] coinType = {1, 5, 10};
  drawPriceMonitor(xOffset + xPriceMonitor, yPriceMonitor);
  for (int index = 0; index < coinType.length; index++) {
    drawMoneyLabel(xOffset + xMoneyLabel, yMoneyLabel[index], coinType[index], numberCoinMonitor[index]);
  }
  drawCancelButton(xOffset+ xCancelButton, yCancelButton);
  drawLightWarning(xOffset + 550, 390);
}
/////////////////////////Draw balance you put it
void drawPriceMonitor(int xMonitor, int yMonitor) {
  int widthMonitor = 150, heightMonitor = 50;
  int xStatus = (int)(xMonitor + 0.2 * widthMonitor);
  int xMoney = (int)(xMonitor + 0.8 * widthMonitor);
  int yText = (int)(yMonitor + 0.5 * heightMonitor);
  //Draw black background
  noStroke();
  fill(0);
  rect(xMonitor, yMonitor, widthMonitor, heightMonitor);
  //Draw Text
  fill(255);
  textAlign(CENTER, CENTER);
  text("Balance", xStatus, yText);
  text(monitorMoney, xMoney, yText);
}
/////////////////////////////////Draw money selection to increase
void drawMoneyLabel(int xLogo, int yLogo, int price, int numberCoins) {
  int widthLabel = 100, heightLabel = 30, space = 20;
  int xLabel = xLogo + space;
  int yLabel = yLogo - heightLabel / 2;
  ////////////////////Draw Coin
  noFill();
  stroke(0);
  strokeWeight(1);
  //Draw coin picture
  drawCoins(xLogo, yLogo, heightLabel, price);
  noStroke();
  ///////////////////////////Draw Background Label
  fill(#860000);
  rect(xLabel, yLabel, widthLabel, heightLabel);
  ////////////////////////////////////////////Draw Increase Tab
  float xAdd = xLabel + 0.75 * widthLabel;
  int widthAdd = int(0.25 * widthLabel);
  fill(#E14938);    
  rect(xAdd, yLabel, 0.25 * widthLabel, heightLabel);
  fill(255);
  //////////////////////////////////////Draw Positive
  drawPositive(xAdd + widthAdd / 2, yLabel + heightLabel / 2, widthAdd * 0.7, widthAdd * 0.2);
  /////////////////////////////////////Draw Text
  textAlign(CENTER, CENTER);
  int xPrice = int(xLabel + widthLabel * 0.375);
  text(numberCoins, xPrice, yLogo);
}
//Draw coin image
void drawCoins(float xLogo, float yLogo, float heightLabel, int type) {
  noStroke();
  if (type == 1) {
    ////////////////Draw one baht
    fill(#C0C0C0);
    ellipse(xLogo, yLogo, heightLabel * 0.75, heightLabel * 0.75);
  } else if (type == 5) {
    ////////////////Draw five baht
    fill(#A0A0A0);
    ellipse(xLogo, yLogo, heightLabel, heightLabel);
    fill(#C0C0C0);
    ellipse(xLogo, yLogo, heightLabel * 0.75, heightLabel * 0.75);
  } else if (type == 10) {
    ////////////////Draw ten baht
    fill(#A0A0A0);
    ellipse(xLogo, yLogo, heightLabel, heightLabel);
    fill(#FDE3A8);
    ellipse(xLogo, yLogo, heightLabel * 0.75, heightLabel * 0.75);
  }
}

void drawPositive(float x, float y, float sizePos, float sizeTab) {
  rect(x - sizeTab/2, y - sizePos/2, sizeTab, sizePos);  //Verical
  rect(x - sizePos/2, y - sizeTab/2, sizePos, sizeTab);  //Horizontal
}

void drawCancelButton(int xButton, int yButton) {
  noStroke();
  fill(#E14938);
  int widthButton = 100, heightButton = 50;
  rect(xButton, yButton, widthButton, heightButton);
  fill(255);
  textAlign(CENTER, CENTER);
  text("CANCEL", xButton + widthButton/2, yButton + heightButton / 2);
}

void drawLightWarning(int xOffset, int yOffset) {
  /////////////////////////////Check products are out?
  boolean productsOut = false, coinOut = false, coinFull = false;
  for (int i = 0; i < numberProducts.length; i++) {
    if (isProductsOut[i]) {
      productsOut = true;  
      break;
    }
  }
  for (int i = 0; i < numberCoinMachine.length; i++) {
    if (numberCoinMachine[i] == 0) {
      coinOut = true;  
      break;
    }
  }
  for (int i = 0; i < numberCoinMachine.length; i++) {
    int maxValue = 100;
    if (numberCoinMachine[i] == maxValue) {
      coinFull = true;  
      break;
    }
  }
  ////////////////////////Draw Out of product Light
  int sizeLight = 10;
  if (productsOut) {
    fill(#E84C3D);
  } else {
    fill(#7E8C8D);
  }
  ellipse(xOffset, yOffset, sizeLight, sizeLight);
  textAlign(LEFT, CENTER);
  fill(0);
  text("Out of product", xOffset + 20, yOffset);
  ///////////////////////Draw Out of coin Light
  if (coinOut) {
    fill(#E84C3D);
  } else {
    fill(#7E8C8D);
  }
  ellipse(xOffset, yOffset + 15, sizeLight, sizeLight);
  fill(0);
  text("Out of coin", xOffset + 20, yOffset + 15);
  /////////////////////////Draw Full of coin Light
  if (coinFull) {
    fill(#E84C3D);
  } else {
    fill(#7E8C8D);
  }
  ellipse(xOffset, yOffset + 30, sizeLight, sizeLight);
  fill(0);
  text("Full of coin", xOffset + 20, yOffset + 30);
}
////////////////////////////////////////Admin Mode
void drawAdmin(int xOffset, int xMinLeft, int xMaxLeft, int xMinRight, int xMaxRight) {
  int yProduct = 75, yCoin = 250, yPrice = 75, ySold = 225, heightTerminal = 110;
  drawRemainingProduct(xOffset, yProduct);
  drawRemainingCoin(xOffset, yCoin);
  drawPriceProduct(xOffset, yPrice);
  drawSoldProduct(xOffset, ySold);
  drawAdminTerminal(xOffset, heightTerminal);
  int[] yProductSlide  = {90, 115, 140, 165, 190};
  int[] yCoinSlide = {265, 290, 315};
  //Set min and max of x
  xMinLeft = xMinLeft + xOffset;
  xMaxLeft = xMaxLeft + xOffset;
  xMinRight = xMinRight + xOffset;
  xMaxRight = xMaxRight + xOffset;
  //Draw Slide bar
  drawSlides(xOffset, xSlideProducts, yProductSlide, xMinLeft, xMaxLeft);
  drawSlides(xOffset, xSlideCoins, yCoinSlide, xMinLeft, xMaxLeft);
  drawSlides(xOffset, xSlidePrices, yProductSlide, xMinRight, xMaxRight);
}

void drawSlides(int xAllOffset, int[] xOffset, int[] yOffset, int xMin, int xMax) {
  for (int index = 0; index < yOffset.length; index++) {
    drawSlideBar(xAllOffset + xOffset[index], yOffset[index], xMin, xMax);
  }
}

void drawSlideBar(int xOffset, int yOffset, int xMin, int xMax) {
  int sizePoint = 15;
  //////////////////Draw line min to max
  stroke(#808080);
  strokeWeight(3);
  line(xMin, yOffset, xMax, yOffset);
  /////////////////Draw line min to its position
  strokeWeight(7);
  stroke(#A60C00);
  line(xMin, yOffset, xOffset, yOffset);
  ///////////////Draw Circle
  noStroke();
  fill(#A60C00);
  ellipse(xOffset, yOffset, sizePoint, sizePoint);
}

void drawRemainingProduct(int xOffset, int yStartText) {
  String[] text = {"Eggs", "Boiled eggs", "Fried eggs", "Omelets", "Stuffed eggs"};
  int xText = xOffset + 25, xValue = xOffset + 315, space = 25;
  fill(0);
  textAlign(LEFT, BOTTOM);
  text("Remaining Product", xOffset + 10, yStartText);
  for (int index = 0; index < text.length; index++) {
    text(text[index], xText, yStartText + (index + 1) * space);
    text(numberProducts[index], xValue, yStartText + (index + 1) * space);
  }
  int sumProducts = 0;
  for (int index = 0; index < text.length; index++) {
    sumProducts += numberProducts[index];
  }
  text("Sum of products", xText, yStartText + 150);
  text(sumProducts, xValue, yStartText + 150);
}

void drawRemainingCoin(int xOffset, int yStartText) {
  String[] text = {"1 Bath", "5 Bath", "10 Baht"};
  int xText = xOffset + 25, xValue = xOffset + 315, space = 25;
  fill(0);
  textAlign(LEFT, BOTTOM);
  text("Remaining Coin", xOffset + 10, yStartText);
  for (int index = 0; index < text.length; index++) {
    text(text[index], xText, yStartText + (index+1) * space);
    text(numberCoinMachine[index], xValue, yStartText + (index + 1) * space);
  }
  int sumCoin = 0;
  int[] priceCoin = {1, 5, 10};
  for (int index = 0; index < text.length; index++) {
    sumCoin += numberCoinMachine[index] * priceCoin[index];
  }
  text("Sum of value", xText, yStartText + 100);
  text(sumCoin, xValue, yStartText + 100);
}

void drawPriceProduct(int xOffset, int yStartText) {
  String[] text = {"Eggs", "Boiled eggs", "Fried eggs", "Omelets", "Stuffed eggs"};
  int xText = xOffset + 375, space = 25, xValue = xOffset + 665;
  fill(0);
  textAlign(LEFT, BOTTOM);
  text("Price Product", xOffset + 360, yStartText);
  for (int index = 0; index < text.length; index++) {
    text(text[index], xText, yStartText + (index + 1) * space);
    text(priceProducts[index], xValue, yStartText + (index + 1) * space);
  }
}

void drawSoldProduct(int xOffset, int yStartText) {
  String[] text = {"Eggs", "Boiled eggs", "Fried eggs", "Omelets", "Stuffed eggs"};
  int xText = xOffset + 375, xValue = xOffset + 665, space = 25;
  fill(0);
  textAlign(LEFT, BOTTOM);
  text("Sold Product", xOffset + 360, yStartText);
  for (int index = 0; index < text.length; index++) {
    text(text[index], xText, yStartText + (index + 1) * space);
    text(soldProducts[index], xValue, yStartText + (index + 1) * space);
  }
  text("Balance ", xText, yStartText + 150);
  text(soldBalance, xValue, yStartText + 150);
}
///////////////////////////////////////History text box
void drawAdminTerminal(int xOffset, int heightBox) {  
  int widthBox = width, yBox = height - heightBox, xText = xOffset + 10, space = 20;
  noStroke();
  fill(#B85400);
  rect(xOffset, yBox, widthBox, heightBox);
  textAlign(LEFT, TOP);
  fill(255);
  text("History of transactions", xText, yBox + 10);
  for (int index = 0; index < adminTexts.length; index++) {
    if (adminTexts[index] != null) {
      text(adminTexts[index], xText, yBox + 10 + (index + 1) * space);
    }
  }
  int xReset = 600;
  int widthReset = 100;
  drawResetButton(xOffset + xReset, yBox, widthReset, heightBox);
}

void drawResetButton(int x, int y, int widthButton, int heightButton) {
  /////////////////////Reset Machine
  fill(#F39C11);
  noStroke();
  rect(x, y, widthButton, heightButton/2);
  fill(255);
  textAlign(CENTER, CENTER);
  text("Reset Machine", x + widthButton/2, y + heightButton / 4);
  /////////////////////Reset Simulation
  fill(#F39C11);
  noStroke();
  rect(x, y + heightButton / 2, widthButton, heightButton/2);
  fill(255);
  textAlign(CENTER, CENTER);
  text("Reset Simulation", x + widthButton/2, y + 3 * heightButton / 4);
  ////////////////////////Line to divide Button
  stroke(#FFCE4B);
  strokeWeight(3);
  strokeCap(SQUARE);
  line(x, y + heightButton / 2, x + widthButton, y + heightButton / 2);
}
//////////////////////////////////////////////////////////Mouse Control
void mouseReleased() {
  if (mode == "ADMIN") {  //Mode Admin
    setFalseSlide();             //Cancel to move x slide bar
    int xReset = 600, yReset = 440, widthReset = 100, heightReset = 110;
    resetMouse(xReset, yReset, widthReset, heightReset);
  }
}
void setFalseSlide() {
  for (int index = 0; index < isSlideProducts.length; index++) {
    isSlideProducts[index] = false;
  }
  for (int index = 0; index < isSlideCoins.length; index++) {
    isSlideCoins[index] = false;
  }
  for (int index = 0; index < xSlidePrices.length; index++) {
    isSlidePrices[index] = false;
  }
}
void resetMouse(int xButton, int yButton, int widthButton, int heightButton) {
  if (isClick(xButton, yButton, widthButton, heightButton / 2)) {
    resetMachine();
  } else if (isClick(xButton, yButton + heightButton/2, widthButton, heightButton / 2)) {
    resetSimulate();
  }
}
////////////////////////////////////////////////////////////////////
void mouseClicked() {
}
////////////////////////////////////////////////////////////////////
void mousePressed() {
  /////////////////////////////////////Choose Mode
  int xMachineTab = 0, xAdminTab = width/2;
  int yAboveTab = 0, widthAbove = width/2, heightTab = 50;
  if (isClick(xMachineTab, yAboveTab, widthAbove, heightTab)) {
    mode = "MACHINE";
  } else if (isClick(xAdminTab, yAboveTab, widthAbove, heightTab)) {
    mode = "ADMIN";
  }
  if (mode == "MACHINE") {    //Mode Machine
    ///////////////////////Check product selection
    int xProducts[] = {50, 200, 350, 125, 275};
    int yProducts[] = {75, 75, 75, 250, 250};
    productSelectedMouse(xProducts, yProducts);
    ////////////////////////Check increase coin to input machine
    int xCoinMonitor = 645;
    int[] yCoinMonitors = {175, 225, 275};
    coinMonitorNumberMouse(xCoinMonitor, yCoinMonitors);
    //////////////////////////////////Check cancel
    int xCancel = 550, yCancel = 325;
    int widthCancel = 100, heightCancel = 50;
    cancelMouse(xCancel, yCancel, widthCancel, heightCancel);
  } else if (mode == "ADMIN") {
    int[] yProductSlide  = {90, 115, 140, 165, 190};
    int[] yCoinSlide = {265, 290, 315};
    int sizePoint = 10;
    //////////////////////////////////Check click on product slide bar
    for (int i = 0; i < xSlideProducts.length; i++) {
      if (isClickRadius(xSlideProducts[i], yProductSlide[i], sizePoint)) {
        isSlideProducts[i] = true;
      }
    }
    //////////////////////////////////Check click on coin in machine slide bar
    for (int i = 0; i < xSlideCoins.length; i++) {
      if (isClickRadius(xSlideCoins[i], yCoinSlide[i], sizePoint)) {
        isSlideCoins[i] = true;
      }
    }
    //////////////////////////////////Check click on price slide bar
    for (int i = 0; i < xSlidePrices.length; i++) {
      if (isClickRadius(xSlidePrices[i], yProductSlide[i], sizePoint)) {
        isSlidePrices[i] = true;
      }
    }
  }
}
/////////////////////////////////////////////////////////////////////////
void mouseDragged() {
  if (mode == "ADMIN") {
    int xMinLeft = 100, xMaxLeft = 300, xMinRight = 450, xMaxRight = 650;
    draggedSlide(isSlideProducts, xSlideProducts, xMinLeft, xMaxLeft);
    checkXSlide(numberProducts, xSlideProducts, xMinLeft, xMaxLeft, 0, 100);

    draggedSlide(isSlideCoins, xSlideCoins, xMinLeft, xMaxLeft);
    checkXSlide(numberCoinMachine, xSlideCoins, xMinLeft, xMaxLeft, 0, 100);

    draggedSlide(isSlidePrices, xSlidePrices, xMinRight, xMaxRight);
    checkXSlide(priceProducts, xSlidePrices, xMinRight, xMaxRight, 0, 100);
  }
}//////////////////////////////////////Check when drag on slide bar
void draggedSlide(boolean[] isSlides, int[] xSlides, int xMin, int xMax) {
  int xSlide = mouseX;
  if (xSlide < xMin) {
    xSlide = xMin;
  } else if (xSlide > xMax) {
    xSlide = xMax;
  }
  for (int i = 0; i < xSlides.length; i++) {
    if (isSlides[i]) {
      xSlides[i] = xSlide;
    }
  }
}