package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

import javax.smartcardio.TerminalFactorySpi;

@SuppressWarnings("restriction")
public class NokiaProvider extends Provider {

	private static final long serialVersionUID = -6989957570993710912L;
    public static final String PROVIDER_NAME = "NokiaProvider";
    public static final double PROVIDER_VERSION = 1.0d;
    public static final String PROVIDER_INFO = "Nokia Phone Terminal Provider";
	public static final Class<? extends TerminalFactorySpi> FACTORY_SPI_CLASS = NokiaFactorySpi.class;
	
	
    public NokiaProvider() {
		super(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
		// put("TerminalFactory.MyType", "at.tuwien.nokiaprovider.NokiaSpi");

		AccessController.doPrivileged(new PrivilegedAction<Object>() {

			public Object run() {
		        put("TerminalFactory." + PROVIDER_NAME, FACTORY_SPI_CLASS.getCanonicalName());
				return null;
			}
		});

	}
}

