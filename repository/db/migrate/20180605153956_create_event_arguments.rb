class CreateEventArguments < ActiveRecord::Migration[5.1]
  def change
    create_table :event_arguments do |t|
      t.string :type
      t.string :value
      t.references :event, foreign_key: true

      t.timestamps
    end
  end
end
