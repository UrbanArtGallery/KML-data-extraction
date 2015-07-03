@Grapes(
	@Grab(group='org.jsoup', module='jsoup', version='1.8.2')
)

import org.jsoup.Jsoup
import groovy.json.*

File dataFile = new File("doc.kml")
def data = new XmlParser().parseText(dataFile.text)

def getValue = { row ->
	return row.select('td').last().text()
}

def placemarks = data.Document.Folder.Placemark
def parsedData = placemarks.collect { placemark ->

	def description = Jsoup.parse(placemark.description.text())
	def rows = description.select('table').last().select('tr')
	def point = placemark.Point.coordinates.text().split(',')
	//println lat
	//println artist
	return [
		artist: getValue(rows[0]),
		title: getValue(rows[1]),
		date: getValue(rows[2]),
		materials: getValue(rows[3]),
		address: getValue(rows[4]),
		description: getValue(rows[5]),
		type: getValue(rows[6]),
		url1: rows[7].select('td').last().select('a').attr('href'),
		url2: rows[8].select('td').last().select('a').attr('href'),
		location: [
			lat: Double.parseDouble(point[1].trim()),
			long: Double.parseDouble(point[0].trim())
		]
	]
}
println parsedData

new File('parsedData.json').write(JsonOutput.toJson(parsedData))
//Document doc = Jsoup.parse(html)