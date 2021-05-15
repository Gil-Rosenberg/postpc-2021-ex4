# Question

Testing the CalculateRootsService for good input is pretty easy - we pass in a number and we expect 
a broadcast intent with the roots.
Testing for big prime numbers can be frustrating - currently the service is hard-coded to run for 
20 seconds before giving up, which would make the tests run for too long.

What would you change in the code in order to let the service run for maximum 200ms in tests 
environments, but continue to run for 20sec max in the real app (production environment)?

# Answer:
I would specify timeout period of a certain test case, “timeout” attribute is mentioned on 
annotation @Test. For example, @Test(timeout = 200).
Timeout time is specified in milliseconds.

# My promise:

I pledge the highest level of ethical principles in support of academic excellence.  
I ensure that all of my work reflects my own abilities and not those of someone else.

~ Gil Rosenberg ~