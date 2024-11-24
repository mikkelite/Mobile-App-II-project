package com.example.project300352053.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class EntryOb (val Entry: Entry){
       var uid=Entry.uid
       var amount=Entry.amount
       var description=Entry.description
       var type=Entry.type
       var dateTime=Entry.dateTime

}