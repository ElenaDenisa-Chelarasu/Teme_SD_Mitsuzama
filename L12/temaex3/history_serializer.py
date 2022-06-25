import sqlite3

if __name__ == '__main__':
    con = sqlite3.connect('input/places.sqlite')
    c = con.cursor()
    with open('input/history.txt', 'w') as f:
        f.writelines([it[0]+'\n' for it in c.execute('SELECT url FROM moz_places')])
    c.close()
    con.close()
