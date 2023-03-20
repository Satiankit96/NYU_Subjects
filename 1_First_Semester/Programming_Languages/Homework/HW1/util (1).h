/* Additional functionality to the calculator */
/* Some of the functionality is already built into C like the trig and logarithmic functions, however conversions and such are not. */

// Convert GBP to USD	 
double gbp_to_usd(double gbp)   { return gbp * 1.57; }
// Convert GBP to EURO
double gbp_to_euro(double gbp)  { return gbp * 1.26; }

// Convert USD to GBP
double usd_to_gbp(double usd)   { return usd * 0.64; }
// Convert USD to EURO
double usd_to_euro(double usd)  { return usd * 0.80; }

// Convert EURO to GBP
double euro_to_gbp(double euro) { return euro * 0.79; }
// Convert EURO to USD
double euro_to_usd(double euro) { return euro * 1.24; }

// Convert celsius to fahrenheit
double cel_to_fah(double cel) 	{ return cel * 9 / 5 + 32; }
// Convert fahrenheit to celsius
double fah_to_cel(double fah) 	{ return (fah - 32) * 5 / 9; }

// Convert kilometers to miles
double km_to_m(double km) 		  { return km * 0.62137; }
// Convert miles to kilometers 
double m_to_km(double m)  		  { return m / 0.62137;} 

// Calculate factorial
double factorial(double n) 
{
	double x; double f=1;
	
	for (x=1; x<=n; x++) { 
		f *= x; 
	}
	
	return f;
}

// Calculate modulus
int modulo(double x, double y) 
{
	return (int)x % (int)y;
}