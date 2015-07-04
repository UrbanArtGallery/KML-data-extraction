@Grapes(
	@Grab(group='org.jsoup', module='jsoup', version='1.8.2')
)

import org.jsoup.Jsoup

File dataFile = new File("doc.kml")
def data = new XmlParser().parseText(dataFile.text)

def getValue = { row ->
	return row.select('td').last().text()
}

def placemarks = data.Document.Folder.Placemark
def outputString = "id\t artist\t title\t date\t materials\t address\t description\t type\t url1\t url2\t loc_lat\t, loc_long\n"
def parsedData = placemarks.each { placemark ->

	def description = Jsoup.parse(placemark.description.text())
	def rows = description.select('table').last().select('tr')
	def point = placemark.Point.coordinates.text().split(',')
	//println lat
	//println artist
	def rowValues = [
		id: placemark.@id,
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

	outputString += "${rowValues.id}\t ${rowValues.artist}\t ${rowValues.title}\t ${rowValues.date}\t ${rowValues.materials}\t ${rowValues.address}\t ${rowValues.description}\t ${rowValues.type}\t ${rowValues.url1}\t ${rowValues.url2}\t ${rowValues.location.lat}\t ${rowValues.location.long}\n"
}



new File('transformed-data.csv').write(outputString)
//Document doc = Jsoup.parse(html)