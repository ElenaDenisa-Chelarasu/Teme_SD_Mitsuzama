FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python

# adaugare fisier sursa pentru microserviciul Bidder
WORKDIR /bidder
ADD Bidder.py $WORKDIR

# comanda de executie este: python <nume_fisier>.py
CMD ["python", "Bidder.py"]
