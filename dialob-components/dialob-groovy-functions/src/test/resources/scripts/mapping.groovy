import io.dialob.groovy.DialobDDRLFunction

class Mapping {
  
  @DialobDDRLFunction
  static String mappingFunction(String val) {
    switch(val) {
      case 'value1': return "4";
      case 'value2': return "10";
      case 'value3': return "20";
      case 'value4': return "30";
      default: return "10"
    }
  }
}
