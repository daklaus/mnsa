package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import javax.smartcardio.*;

@SuppressWarnings("restriction")
public class NokiaFactorySpi extends TerminalFactorySpi {
    
	public NokiaFactorySpi() {
        // initialize as appropriate
    }
   	
	public NokiaFactorySpi(Object parameter) {
         // initialize as appropriate
     }
    
	
    @Override
    protected CardTerminals engineTerminals() {
        return new NokiaTerminals();
    }
    
}