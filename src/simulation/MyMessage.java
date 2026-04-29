package simulation;

import OSPABA.*;

public class MyMessage extends OSPABA.MessageForm
{
	private String typPacienta;
	private double casPrichodu;

	public int idPacienta;
	public static int globalIdCounter = 1;

	public MyMessage(Simulation mySim)
	{
		super(mySim);
		this.idPacienta = globalIdCounter++;
	}

	public MyMessage(MyMessage original)
	{
		super(original);
		// copy() is called in superclass
	}

	@Override
	public MessageForm createCopy()
	{
		return new MyMessage(this);
	}

	@Override
	protected void copy(MessageForm message)
	{
		super.copy(message);
		MyMessage original = (MyMessage)message;
		// Copy attributes
		this.typPacienta = original.typPacienta;
        this.casPrichodu = original.casPrichodu;
		this.idPacienta = original.idPacienta;
	}

	public String getTypPacienta() {
        return typPacienta;
    }

    public void setTypPacienta(String typPacienta) {
        this.typPacienta = typPacienta;
    }

    public double getCasPrichodu() {
        return casPrichodu;
    }

    public void setCasPrichodu(double casPrichodu) {
        this.casPrichodu = casPrichodu;
    }
}
