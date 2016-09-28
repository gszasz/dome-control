PREFIX = /usr/local
DESTDIR = $(PREFIX)/share/DomeControl
BINDIR = $(PREFIX)/bin

all: DomeControl.jar

DomeControl.jar:
	cd src; $(MAKE) all

install: 
	mkdir -p $(DESTDIR)
	cp src/DomeControl.jar scripts/DomeControl.sh $(DESTDIR)
	cp src/defaultProperties src/customProperties src/catalog.dat $(DESTDIR)
	chmod -f +x $(DESTDIR)/DomeControl.sh
	ln -fs $(DESTDIR)/DomeControl.sh $(BINDIR)/DomeControl
	cd icons; $(MAKE) install

uninstall:
	rm -rf $(DESTDIR)
	rm -f $(BINDIR)/DomeControl
	cd icons; $(MAKE) uninstall

clean:
	cd src; $(MAKE) clean
