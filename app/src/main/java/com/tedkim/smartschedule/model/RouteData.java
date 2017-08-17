package com.tedkim.smartschedule.model;

/**
 * Created by tedkim on 2017. 8. 13..
 */

public class RouteData {

    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ClassPojo [result = " + result + "]";
    }

    public class Result
    {
        private int endRadius;
        private int outTrafficCheck;
        private int startRadius;
        private int busCount;
        private Path[] path;
        private double pointDistance;
        private int searchType;
        private int subwayBusCount;
        private int subwayCount;

        public int getEndRadius() {
            return endRadius;
        }

        public void setEndRadius(int endRadius) {
            this.endRadius = endRadius;
        }

        public int getOutTrafficCheck() {
            return outTrafficCheck;
        }

        public void setOutTrafficCheck(int outTrafficCheck) {
            this.outTrafficCheck = outTrafficCheck;
        }

        public int getStartRadius() {
            return startRadius;
        }

        public void setStartRadius(int startRadius) {
            this.startRadius = startRadius;
        }

        public int getBusCount() {
            return busCount;
        }

        public void setBusCount(int busCount) {
            this.busCount = busCount;
        }

        public Path[] getPath() {
            return path;
        }

        public void setPath(Path[] path) {
            this.path = path;
        }

        public double getPointDistance() {
            return pointDistance;
        }

        public void setPointDistance(double pointDistance) {
            this.pointDistance = pointDistance;
        }

        public int getSearchType() {
            return searchType;
        }

        public void setSearchType(int searchType) {
            this.searchType = searchType;
        }

        public int getSubwayBusCount() {
            return subwayBusCount;
        }

        public void setSubwayBusCount(int subwayBusCount) {
            this.subwayBusCount = subwayBusCount;
        }

        public int getSubwayCount() {
            return subwayCount;
        }

        public void setSubwayCount(int subwayCount) {
            this.subwayCount = subwayCount;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [endRadius = "+endRadius+", outTrafficCheck = "+outTrafficCheck+", startRadius = "+startRadius+", busCount = "+busCount+", path = "+path+", pointDistance = "+pointDistance+", searchType = "+searchType+", subwayBusCount = "+subwayBusCount+", subwayCount = "+subwayCount+"]";
        }

        public class Path {

            private SubPath[] subPath;
            private int pathType;
            private Info info;

            public SubPath[] getSubPath() {
                return subPath;
            }

            public void setSubPath(SubPath[] subPath) {
                this.subPath = subPath;
            }

            public int getPathType() {
                return pathType;
            }

            public void setPathType(int pathType) {
                this.pathType = pathType;
            }

            public Info getInfo() {
                return info;
            }

            public void setInfo(Info info) {
                this.info = info;
            }

            @Override
            public String toString()
            {
                return "ClassPojo [subPath = "+subPath+", pathType = "+pathType+", info = "+info+"]";
            }

            public class SubPath {

                private int trafficType;
                private double distance;
                private String sectionTime;
                private String stationCount;
                private Lane lane;
                private PassStopList passStopList;

                public int getTrafficType() {
                    return trafficType;
                }

                public void setTrafficType(int trafficType) {
                    this.trafficType = trafficType;
                }

                public double getDistance() {
                    return distance;
                }

                public void setDistance(double distance) {
                    this.distance = distance;
                }

                public String getSectionTime() {
                    return sectionTime;
                }

                public void setSectionTime(String sectionTime) {
                    this.sectionTime = sectionTime;
                }

                public String getStationCount() {
                    return stationCount;
                }

                public void setStationCount(String stationCount) {
                    this.stationCount = stationCount;
                }

                public Lane getLane() {
                    return lane;
                }

                public void setLane(Lane lane) {
                    this.lane = lane;
                }

                public PassStopList getPassStopList() {
                    return passStopList;
                }

                public void setPassStopList(PassStopList passStopList) {
                    this.passStopList = passStopList;
                }

                @Override
                public String toString()
                {
                    return "ClassPojo [trafficType = "+trafficType+", distance = "+distance+", sectionTime = "+sectionTime+"]";
                }

                public class Lane {

                    private String name;
                    private String busNo;
                    private int type;
                    private int busID;
                    private int subwayCode;
                    private String startName;
                    private double startX;
                    private double startY;
                    private String endName;
                    private double endX;
                    private double endY;
                    private String way;
                    private int wayCode;
                    private String door;
                    private int startID;
                    private int endID;
                    private String startExitNo;
                    private double startExitX;
                    private double startExitY;
                    private String endExitNo;
                    private double endExitX;
                    private double endExitY;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getBusNo() {
                        return busNo;
                    }

                    public void setBusNo(String busNo) {
                        this.busNo = busNo;
                    }

                    public int getType() {
                        return type;
                    }

                    public void setType(int type) {
                        this.type = type;
                    }

                    public int getBusID() {
                        return busID;
                    }

                    public void setBusID(int busID) {
                        this.busID = busID;
                    }

                    public int getSubwayCode() {
                        return subwayCode;
                    }

                    public void setSubwayCode(int subwayCode) {
                        this.subwayCode = subwayCode;
                    }

                    public String getStartName() {
                        return startName;
                    }

                    public void setStartName(String startName) {
                        this.startName = startName;
                    }

                    public double getStartX() {
                        return startX;
                    }

                    public void setStartX(double startX) {
                        this.startX = startX;
                    }

                    public double getStartY() {
                        return startY;
                    }

                    public void setStartY(double startY) {
                        this.startY = startY;
                    }

                    public String getEndName() {
                        return endName;
                    }

                    public void setEndName(String endName) {
                        this.endName = endName;
                    }

                    public double getEndX() {
                        return endX;
                    }

                    public void setEndX(double endX) {
                        this.endX = endX;
                    }

                    public double getEndY() {
                        return endY;
                    }

                    public void setEndY(double endY) {
                        this.endY = endY;
                    }

                    public String getWay() {
                        return way;
                    }

                    public void setWay(String way) {
                        this.way = way;
                    }

                    public int getWayCode() {
                        return wayCode;
                    }

                    public void setWayCode(int wayCode) {
                        this.wayCode = wayCode;
                    }

                    public String getDoor() {
                        return door;
                    }

                    public void setDoor(String door) {
                        this.door = door;
                    }

                    public int getStartID() {
                        return startID;
                    }

                    public void setStartID(int startID) {
                        this.startID = startID;
                    }

                    public int getEndID() {
                        return endID;
                    }

                    public void setEndID(int endID) {
                        this.endID = endID;
                    }

                    public String getStartExitNo() {
                        return startExitNo;
                    }

                    public void setStartExitNo(String startExitNo) {
                        this.startExitNo = startExitNo;
                    }

                    public double getStartExitX() {
                        return startExitX;
                    }

                    public void setStartExitX(double startExitX) {
                        this.startExitX = startExitX;
                    }

                    public double getStartExitY() {
                        return startExitY;
                    }

                    public void setStartExitY(double startExitY) {
                        this.startExitY = startExitY;
                    }

                    public String getEndExitNo() {
                        return endExitNo;
                    }

                    public void setEndExitNo(String endExitNo) {
                        this.endExitNo = endExitNo;
                    }

                    public double getEndExitX() {
                        return endExitX;
                    }

                    public void setEndExitX(double endExitX) {
                        this.endExitX = endExitX;
                    }

                    public double getEndExitY() {
                        return endExitY;
                    }

                    public void setEndExitY(double endExitY) {
                        this.endExitY = endExitY;
                    }

                    @Override
                    public String toString()
                    {
                        return "ClassPojo [busID = "+busID+", type = "+type+", busNo = "+busNo+"]";
                    }
                }

                public class PassStopList {

                    private Stations[] stations;
                    public Stations[] getStations() {
                        return stations;
                    }

                    public void setStations(Stations[] stations) {
                        this.stations = stations;
                    }

                    public class Stations {

                        private String index;
                        private int stationID;
                        private String stationName;
                        private String y;
                        private String x;

                        public String getIndex() {
                            return index;
                        }

                        public void setIndex(String index) {
                            this.index = index;
                        }

                        public int getStationID() {
                            return stationID;
                        }

                        public void setStationID(int stationID) {
                            this.stationID = stationID;
                        }

                        public String getStationName() {
                            return stationName;
                        }

                        public void setStationName(String stationName) {
                            this.stationName = stationName;
                        }

                        public String getY() {
                            return y;
                        }

                        public void setY(String y) {
                            this.y = y;
                        }

                        public String getX() {
                            return x;
                        }

                        public void setX(String x) {
                            this.x = x;
                        }

                        @Override
                        public String toString() {
                            return "ClassPojo [index = " + index + ", stationID = " + stationID + ", stationName = " + stationName + ", y = " + y + ", x = " + x + "]";
                        }

                    }

                    @Override
                    public String toString()
                    {
                        return "ClassPojo [stations = "+stations+"]";
                    }
                }
            }

            public class Info {

                private int totalWalk;
                private int totalTime;
                private int payment;
                private int totalStationCount;
                private int busStationCount;
                private int totalWalkTime;
                private int busTransitCount;
                private int subwayTransitCount;
                private int subwayStationCount;
                private double totalDistance;
                private double trafficDistance;
                private String mapObj;
                private String firstStartStation;
                private String lastEndStation;

                public int getTotalWalk() {
                    return totalWalk;
                }

                public void setTotalWalk(int totalWalk) {
                    this.totalWalk = totalWalk;
                }

                public int getTotalTime() {
                    return totalTime;
                }

                public void setTotalTime(int totalTime) {
                    this.totalTime = totalTime;
                }

                public int getPayment() {
                    return payment;
                }

                public void setPayment(int payment) {
                    this.payment = payment;
                }

                public int getTotalStationCount() {
                    return totalStationCount;
                }

                public void setTotalStationCount(int totalStationCount) {
                    this.totalStationCount = totalStationCount;
                }

                public int getBusStationCount() {
                    return busStationCount;
                }

                public void setBusStationCount(int busStationCount) {
                    this.busStationCount = busStationCount;
                }

                public int getTotalWalkTime() {
                    return totalWalkTime;
                }

                public void setTotalWalkTime(int totalWalkTime) {
                    this.totalWalkTime = totalWalkTime;
                }

                public int getBusTransitCount() {
                    return busTransitCount;
                }

                public void setBusTransitCount(int busTransitCount) {
                    this.busTransitCount = busTransitCount;
                }

                public int getSubwayTransitCount() {
                    return subwayTransitCount;
                }

                public void setSubwayTransitCount(int subwayTransitCount) {
                    this.subwayTransitCount = subwayTransitCount;
                }

                public int getSubwayStationCount() {
                    return subwayStationCount;
                }

                public void setSubwayStationCount(int subwayStationCount) {
                    this.subwayStationCount = subwayStationCount;
                }

                public double getTotalDistance() {
                    return totalDistance;
                }

                public void setTotalDistance(double totalDistance) {
                    this.totalDistance = totalDistance;
                }

                public double getTrafficDistance() {
                    return trafficDistance;
                }

                public void setTrafficDistance(double trafficDistance) {
                    this.trafficDistance = trafficDistance;
                }

                public String getMapObj() {
                    return mapObj;
                }

                public void setMapObj(String mapObj) {
                    this.mapObj = mapObj;
                }

                public String getFirstStartStation() {
                    return firstStartStation;
                }

                public void setFirstStartStation(String firstStartStation) {
                    this.firstStartStation = firstStartStation;
                }

                public String getLastEndStation() {
                    return lastEndStation;
                }

                public void setLastEndStation(String lastEndStation) {
                    this.lastEndStation = lastEndStation;
                }

                @Override
                public String toString()
                {
                    return "ClassPojo [payment = "+payment+", totalStationCount = "+totalStationCount+", busStationCount = "+busStationCount+", totalWalkTime = "+totalWalkTime+", busTransitCount = "+busTransitCount+", totalDistance = "+totalDistance+", firstStartStation = "+firstStartStation+", mapObj = "+mapObj+", subwayStationCount = "+subwayStationCount+", totalWalk = "+totalWalk+", subwayTransitCount = "+subwayTransitCount+", trafficDistance = "+trafficDistance+", lastEndStation = "+lastEndStation+", totalTime = "+totalTime+"]";
                }
            }
        }
    }
}
