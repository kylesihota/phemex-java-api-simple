# phemex-java-api-simple
A straightforward pair of classes to access the Phemex API to execute orders and retrieve price.

## Installation:
1. Add `HashString.java` and `WebClient.java` classes to your src folder (or the directory in which it is desired). Note that a `WebClient` needs to be initialized while`HashString` does not as it has static methods. 
2. Enter your `PHEMEX_SECRET` and `PHEMEX_KEY` in `WebClient`. If you need to create keys, visit Phemex's official ["How do I create an API Key?"](https://phemex.com/user-guides/how-do-i-create-an-api-key).

## Opening a Trade
1. Initialize a `WebClient` with `WebClient client = new WebClient()` . (Note: that the parameters are always empty.)
2. Call the `openTrade` method and pass in  `int size, String symbol` with `client.openTrade(size, symbol)`. Enter positive `size` for longs and negative `size` for shorts. (Note: to ensure you are passing in your desired size and symbol, visit the documentation for more information. This will always be the size after leverage is applied (the default is 1x). An example is provided below.)
3. Response JSON from Phemex will be printed in Console. To view a list of response codes, [click here.](https://github.com/phemex/phemex-api-docs/blob/master/TradingErrorCode.md).

###### Example
`client.openTrade(100, BTCUSD)` will open a long position on BTCUSD (BTC Margined) equivalent to 100 USD at the time of trade. By default, leverage is 1x which means the margin in this position is equivalent to 100 USD (it is denominated in BTC). If leverage was 10x, margin would be equivalent to 10 USD.

## Getting price 
1. Initialize a WebClient with `WebClient client = new WebClient()` . Note that the parameters are always empty.
2. Call the `getPrice` method and pass in a `String symbol` with `client.getPrice(symbol)`. To view a list of supported symbols, visit the official [Phemex API documentation symbol list](https://github.com/phemex/phemex-api-docs/blob/master/Public-Contract-API-en.md#fieldexplained).
3. Price will be returned as a double.

###### Example
`client.getPrice(BTCUSD)` will return the price of BTCUSD as a double.

## Phemex API Documentation
To visit the official Phemex API documentation, [click here](https://github.com/phemex/phemex-api-docs/blob/master/Public-Contract-API-en.md#general).






