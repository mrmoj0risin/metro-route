import core.Line;
import core.Station;

import java.util.*;
import java.util.stream.Collectors;

public class StationIndex {
    private final Map<Integer, Line> number2line;
    private final TreeSet<Station> stations;
    private final Map<Station, TreeSet<Station>> connections;

    public StationIndex() {
        number2line = new HashMap<>();
        stations = new TreeSet<>();
        connections = new TreeMap<>();
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public void addLine(Line line) {
        number2line.put(line.getNumber(), line);
    }

    public void addConnection(List<Station> stations) {
        for (Station station : stations) {
            if (!connections.containsKey(station)) {
                connections.put(station, new TreeSet<>());
            }
            TreeSet<Station> connectedStations = connections.get(station);
            connectedStations.addAll(stations.stream()
                    .filter(s -> !s.equals(station)).collect(Collectors.toList()));
        }
    }

    public Line getLine(int number) {
        return number2line.get(number);
    }

    public Station getStation(String name) {
        for (Station station : stations) {
            if (station.getName().equalsIgnoreCase(name)) {
                return station;
            }
        }
        return null;
    }

    public Station getStation(String name, int lineNumber) {
        Station query = new Station(name, getLine(lineNumber));
        Station station = stations.ceiling(query);
        return station.equals(query) ? station : null;
    }

    public Set<Station> getConnectedStations(Station station) {
//        parseLineConnections();
        return connections.containsKey(station) ?
                connections.get(station) : new TreeSet<>();
    }

//    public boolean lineConnected(Station station1, Station station2){
//
//        Line line1 =  station1.getLine();
//        Line line2 =  station2.getLine();
//
//
//
//        return true;
//    }

//    public void parseLineConnections(){
//        Map<Line,List<Line>> connectionsList = new TreeMap<>() ;
//
//        for (Map.Entry<Station, TreeSet<Station>> entry: connections.entrySet()){
//
//            List<Line> lineList = new ArrayList<>();
//            Line key = entry.getKey().getLine();
//            if (entry.getValue().size() > 1){
//                entry.getValue().forEach(a -> lineList.add(a.getLine()));
//            }
//            else {
//                if (entry.getValue().pollLast().getLine() != null) {
//                    lineList.add( entry.getValue().pollLast().getLine());
//                }
//            }
//            connectionsList.put(key,lineList);
//        }
//
//        for (Map.Entry<Line,List<Line>> entry: connectionsList.entrySet()){
//            System.out.println(entry.getKey() + " - " );
//            entry.getValue().forEach(System.out::println);
//        }
//
//    }
}
