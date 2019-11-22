class CreateEvents < ActiveRecord::Migration[5.1]
  def change
    create_table :events do |t|
      t.string :origin_class
      t.string :destination_class
      t.string :origin_hash
      t.string :destination_hash
      t.datetime :timestamp
      t.string :kind
      t.string :signature
      t.string :source_location
      t.string :class_instance

      t.timestamps
    end
  end
end
